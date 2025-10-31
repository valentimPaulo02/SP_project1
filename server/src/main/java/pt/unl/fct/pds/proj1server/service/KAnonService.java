package pt.unl.fct.pds.proj1server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.unl.fct.pds.proj1server.model.MedDataDeid;
import pt.unl.fct.pds.proj1server.model.MedDataKAnon;
import pt.unl.fct.pds.proj1server.repository.MedDataDeidRepository;
import pt.unl.fct.pds.proj1server.repository.MedDataKAnonRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KAnonService {

    private final MedDataDeidRepository deidRepository;
    private final MedDataKAnonRepository kanonRepository;

    @Value("${privacy.k}")
    private int k;

    public KAnonService(MedDataDeidRepository deidRepository,
                        MedDataKAnonRepository kanonRepository) {
        this.deidRepository = deidRepository;
        this.kanonRepository = kanonRepository;
    }

    public void runKAnonymization() {
        List<MedDataDeid> deidRep = deidRepository.findAll();
        kanonRepository.deleteAll();

        List<Row> data = new ArrayList<>(deidRep.size());
        for (MedDataDeid d : deidRep) {
            data.add(new Row(
                    d.getId(),
                    d.getAge(),
                    d.getGender(),
                    d.getPostalCode(),
                    d.getDiagnosis()
            ));
        }
        List<List<Row>> partitions = splitPartition(data, k);

        List<MedDataKAnon> out = new ArrayList<>();
        for (List<Row> part : partitions) {
            Generalization g = buildGeneralization(part);
            for (Row r : part) {
                MedDataKAnon x = new MedDataKAnon();
                x.setAge(g.age);                 
                x.setGender(g.gender);         
                x.setPostalCode(g.postalCode);       
                x.setDiagnosis(r.diagnosis);
                out.add(x);
            }
        }
        kanonRepository.saveAll(out);
    }


    
    // Algorithm functions ---------------------------------------------------
    private List<List<Row>> splitPartition(List<Row> rows, int k) {
        if (rows.size() <= 2*k - 1) {
            return List.of(rows);
        }

        Dimension best = chooseBestSplitDimension(rows);
        if (best == Dimension.NONE) {
            return List.of(rows);
        }

        SplitResult sr = split(rows, best);
        if (sr.left.size() < k || sr.right.size() < k) {
            return List.of(rows);
        }

        List<List<Row>> result = new ArrayList<>();
        result.addAll(splitPartition(sr.left,  k));
        result.addAll(splitPartition(sr.right, k));
        return result;
    }

    private Dimension chooseBestSplitDimension(List<Row> rows) {
        int minAge = rows.stream().mapToInt(r -> r.age).min().orElse(0);
        int maxAge = rows.stream().mapToInt(r -> r.age).max().orElse(0);
        double ageSpan = maxAge - minAge;

        long genderDistinct = rows.stream().map(r -> r.gender).distinct().count();

        String prefix = commonPrefix(rows.stream().map(r -> r.postal).toList());
        int maxPlen = rows.stream().mapToInt(r -> r.postal.length()).max().orElse(prefix.length());
        double postalSpan = Math.max(0, maxPlen - prefix.length());

        Dimension best = Dimension.NONE;
        double bestVal = -1;

        if (ageSpan > bestVal && ageSpan > 0) { best = Dimension.AGE; bestVal = ageSpan; }
        if (postalSpan > bestVal && postalSpan > 0) { best = Dimension.POSTAL; bestVal = postalSpan; }
        if (genderDistinct > bestVal && genderDistinct > 1) { best = Dimension.GENDER; bestVal = genderDistinct; }

        return best;
    }

    private SplitResult split(List<Row> rows, Dimension d) {
        List<Row> left = new ArrayList<>(), right = new ArrayList<>();
        switch (d) {
            case AGE -> {
                List<Row> sorted = rows.stream().sorted(Comparator.comparingInt(r -> r.age)).toList();
                int mid = sorted.size() / 2;
                left.addAll(sorted.subList(0, mid));
                right.addAll(sorted.subList(mid, sorted.size()));
            }
            case POSTAL -> {
                List<Row> sorted = rows.stream().sorted(Comparator.comparing(r -> r.postal)).toList();
                int mid = sorted.size() / 2;
                left.addAll(sorted.subList(0, mid));
                right.addAll(sorted.subList(mid, sorted.size()));
            }
            case GENDER -> {
                Map<String, List<Row>> by = rows.stream().collect(Collectors.groupingBy(r -> r.gender));
                List<Map.Entry<String, List<Row>>> groups = new ArrayList<>(by.entrySet());
                groups.sort((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()));
                if (groups.size() == 1) {
                    left.addAll(rows);
                } else {
                    left.addAll(groups.get(0).getValue());
                    for (int i = 1; i < groups.size(); i++) right.addAll(groups.get(i).getValue());
                }
            }
            default -> left.addAll(rows);
        }
        return new SplitResult(left, right);
    }

    private Generalization buildGeneralization(List<Row> part) {

        // generalize age - ( min - max )
        int minAge = part.stream().mapToInt(r -> r.age).min().orElse(0);
        int maxAge = part.stream().mapToInt(r -> r.age).max().orElse(0);
        String ageGen = (minAge == maxAge) ? String.valueOf(minAge) : minAge + "-" + maxAge;

        // generalize gender - ( male/female/other )
        boolean hasMale = false, hasFemale = false, hasOther = false;
        for (Row r : part) {
            String g = collapseGender(r.gender);
            if ("Male".equals(g)) hasMale = true;
            else if ("Female".equals(g)) hasFemale = true;
            else hasOther = true;
        }
        StringBuilder gsb = new StringBuilder();
        if (hasMale)   gsb.append("Male");
        if (hasFemale) { if (gsb.length() > 0) gsb.append('/'); gsb.append("Female"); }
        if (hasOther)  { if (gsb.length() > 0) gsb.append('/'); gsb.append("Other"); }
        String genderGen = gsb.length() == 0 ? "Other" : gsb.toString();

        // generalize postal code - ( *********, always 9 digits even when only has 5 )
        List<String> postals = part.stream().map(r -> r.postal).toList();
        String prefix = commonPrefix(postals);
        int maxLen = postals.stream().mapToInt(String::length).max().orElse(prefix.length());
        StringBuilder sb = new StringBuilder(prefix);
        while (sb.length() < maxLen) sb.append('*');
        String postalGen = sb.toString();

        return new Generalization(ageGen, genderGen, postalGen);
    }

    private static String commonPrefix(List<String> vals) {
        if (vals.isEmpty()) return "";
        String p = vals.get(0);
        for (int i = 1; i < vals.size(); i++) {
            String v = vals.get(i);
            int j = 0, lim = Math.min(p.length(), v.length());
            while (j < lim && p.charAt(j) == v.charAt(j)) j++;
            p = p.substring(0, j);
            if (p.isEmpty()) break;
        }
        return p;
    }


    // DTOs and helpers ---------------------------------
    private static String collapseGender(String gender) {
        return switch (gender.trim().toLowerCase()) {
            case "male", "m" -> "Male";
            case "female", "f" -> "Female";
            default -> "Other";
        };
    }

    static class Row {
        long id; int age; String gender; String postal; String diagnosis;

        Row(long id, int age, String gender, String postal, String diagnosis) {
            this.id = id; this.age = age; this.gender = gender; this.postal = postal; this.diagnosis = diagnosis;
        }

        @Override
        public String toString() {
            return String.format("{id=%d, age=%d, gender=%s, postal=%s, diagnosis=%s}",
                    id, age, gender, postal, diagnosis);
        }
    }

    enum Dimension { AGE, GENDER, POSTAL, NONE }
    record SplitResult(List<Row> left, List<Row> right) {}
    record Generalization(String age, String gender, String postalCode) {}
}