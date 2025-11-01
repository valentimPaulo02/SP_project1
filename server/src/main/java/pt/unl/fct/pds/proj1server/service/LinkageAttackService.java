package pt.unl.fct.pds.proj1server.service;

import org.springframework.stereotype.Service;
import pt.unl.fct.pds.proj1server.model.MedData;
import pt.unl.fct.pds.proj1server.model.MedDataDeid;
import pt.unl.fct.pds.proj1server.model.WorkData;
import pt.unl.fct.pds.proj1server.repository.MedDataDeidRepository;
import pt.unl.fct.pds.proj1server.repository.MedDataRepository;
import pt.unl.fct.pds.proj1server.repository.WorkDataRepository;
import pt.unl.fct.pds.proj1server.DTO.LinkedRow;
import pt.unl.fct.pds.proj1server.DTO.LinkageSummary;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LinkageAttackService {

    private final MedDataDeidRepository medDeidRepo;
    private final WorkDataRepository workRepo;
    private final MedDataRepository medRepo;

    public LinkageAttackService(MedDataDeidRepository medDeidRepo,
                                WorkDataRepository workRepo,
                                MedDataRepository medRepo) {
        this.medDeidRepo = medDeidRepo;
        this.workRepo = workRepo;
        this.medRepo = medRepo;
    }

    public LinkageSummary runLinkageAttack() {

        List<MedDataDeid> medDeid = medDeidRepo.findAll();
        Map<QiKey, Long> medCounts = medDeid.stream()
                .collect(Collectors.groupingBy(
                        obj -> new QiKey(obj.getPostalCode(), obj.getGender()),
                        Collectors.counting()));

        Set<QiKey> medUniqueKeys = medCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == 1L)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Map<QiKey, MedDataDeid> medUniqueByKey = medDeid.stream()
                .filter(obj -> medUniqueKeys.contains(new QiKey(obj.getPostalCode(), obj.getGender())))
                .collect(Collectors.toMap(
                        key -> new QiKey(key.getPostalCode(), key.getGender()),
                        row -> row
                ));


        List<WorkData> work = workRepo.findAll();
        Map<QiKey, Long> workCounts = work.stream()
                .collect(Collectors.groupingBy(
                        obj -> new QiKey(obj.getPostalCode(), obj.getGender()),
                        Collectors.counting()));

        Set<QiKey> workUniqueKeys = workCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == 1L)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Map<QiKey, WorkData> workUniqueByKey = work.stream()
                .filter(obj -> workUniqueKeys.contains(new QiKey(obj.getPostalCode(), obj.getGender())))
                .collect(Collectors.toMap(
                        key -> new QiKey(key.getPostalCode(), key.getGender()),
                        row -> row
                ));


        Set<QiKey> intersection = new HashSet<>(medUniqueByKey.keySet());
        intersection.retainAll(workUniqueByKey.keySet());

        List<LinkedRow> linked = new ArrayList<>(intersection.size());
        for (QiKey key : intersection) {
            MedDataDeid medR = medUniqueByKey.get(key);
            WorkData workR = workUniqueByKey.get(key);
            String candName = ((workR.getFName() + " " + workR.getLName()));

            linked.add(new LinkedRow(
                    medR.getId(),
                    medR.getAge(),
                    medR.getGender(),
                    medR.getPostalCode(),
                    candName
            ));
        }


        List<MedData> med = medRepo.findAll();
        Map<TripleKey, String> confirmByKey = med.stream().collect(Collectors.toMap(
                row -> new TripleKey(row.getAge(), row.getGender(), row.getPostalCode()),
                row -> row.getName()
        ));

        int correct = 0;
        for (LinkedRow L : linked) {
            String name = confirmByKey.get(new TripleKey(L.getAge(), L.getGender(), L.getPostal()));
            L.setTrueName(name);
            boolean ok = name.equalsIgnoreCase(L.getCandidateName());
            L.setCorrect(ok);
            if (ok) correct++;
        }

        int medUniques = medUniqueKeys.size();
        int workUniques = workUniqueKeys.size();
        int totalLinked = linked.size();
        double pctCorrect = totalLinked == 0 ? 0.0 : round1(100.0 * correct / totalLinked);
        double pctReidentified = med.size() == 0 ? 0.0 : round1(100.0 * correct / med.size());
        List<LinkedRow> samples = linked.stream().limit(5).collect(Collectors.toList());

        return new LinkageSummary(
                medUniques,
                workUniques,
                totalLinked,
                correct,
                pctCorrect,
                pctReidentified,
                samples
        );
    }

    // ----- helpers & keys -----
    private static double round1(double v) { return Math.round(v * 10.0) / 10.0; }

    record QiKey(String postal, String gender) { }
    record TripleKey(int age, String gender, String postal) { }
}
