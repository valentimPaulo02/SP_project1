package pt.unl.fct.pds.proj1server.service;

import org.springframework.stereotype.Service;
import pt.unl.fct.pds.proj1server.DTO.LinkedRow;
import pt.unl.fct.pds.proj1server.DTO.LinkageSummary;
import pt.unl.fct.pds.proj1server.model.MedData;
import pt.unl.fct.pds.proj1server.model.MedDataDeid;
import pt.unl.fct.pds.proj1server.model.MedDataKAnon;
import pt.unl.fct.pds.proj1server.model.WorkData;
import pt.unl.fct.pds.proj1server.repository.MedDataDeidRepository;
import pt.unl.fct.pds.proj1server.repository.MedDataKAnonRepository;
import pt.unl.fct.pds.proj1server.repository.MedDataRepository;
import pt.unl.fct.pds.proj1server.repository.WorkDataRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LinkageAttackService {

    private final MedDataDeidRepository medDeidRepo;
    private final MedDataKAnonRepository medKanonRepo;
    private final WorkDataRepository workRepo;
    private final MedDataRepository medRepo;

    public LinkageAttackService(MedDataDeidRepository medDeidRepo,
                                MedDataKAnonRepository medKanonRepo,
                                WorkDataRepository workRepo,
                                MedDataRepository medRepo) {
        this.medDeidRepo = medDeidRepo;
        this.medKanonRepo = medKanonRepo;
        this.workRepo = workRepo;
        this.medRepo = medRepo;
    }

    public LinkageSummary runLinkageAttack(String type) {
        List<? extends Object> medSource;
        if ("kanon".equalsIgnoreCase(type)) {
            medSource = medKanonRepo.findAll();
        } else {
            medSource = medDeidRepo.findAll();
        }

        Map<QiKey, Long> medCounts = medSource.stream()
                .collect(Collectors.groupingBy(
                        obj -> {
                            if (obj instanceof MedDataDeid m) {
                                return new QiKey(m.getPostalCode(), m.getGender());
                            } else {
                                MedDataKAnon m = (MedDataKAnon) obj;
                                return new QiKey(m.getPostalCode(), m.getGender());
                            }
                        },
                        Collectors.counting()));

        Set<QiKey> medUniqueKeys = medCounts.entrySet().stream()
                .filter(e -> e.getValue() == 1L)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Map<QiKey, Object> medUniqueByKey = medSource.stream()
                .filter(obj -> {
                    QiKey key;
                    if (obj instanceof MedDataDeid m) {
                        key = new QiKey(m.getPostalCode(), m.getGender());
                    } else {
                        MedDataKAnon m = (MedDataKAnon) obj;
                        key = new QiKey(m.getPostalCode(), m.getGender());
                    }
                    return medUniqueKeys.contains(key);
                })
                .collect(Collectors.toMap(
                        obj -> {
                                if (obj instanceof MedDataDeid m) {
                                return new QiKey(m.getPostalCode(), m.getGender());
                                } else {
                                MedDataKAnon m = (MedDataKAnon) obj;
                                return new QiKey(m.getPostalCode(), m.getGender());
                                }
                        },
                        obj -> obj
                ));


        List<WorkData> work = workRepo.findAll();
        Map<QiKey, Long> workCounts = work.stream()
                .collect(Collectors.groupingBy(
                        w -> new QiKey(w.getPostalCode(), w.getGender()),
                        Collectors.counting()));

        Set<QiKey> workUniqueKeys = workCounts.entrySet().stream()
                .filter(e -> e.getValue() == 1L)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Map<QiKey, WorkData> workUniqueByKey = work.stream()
                .filter(w -> workUniqueKeys.contains(new QiKey(w.getPostalCode(), w.getGender())))
                .collect(Collectors.toMap(
                        w -> new QiKey(w.getPostalCode(), w.getGender()),
                        w -> w
                ));


        Set<QiKey> intersection = new HashSet<>(medUniqueByKey.keySet());
        intersection.retainAll(workUniqueByKey.keySet());

        List<LinkedRow> linked = new ArrayList<>(intersection.size());
        for (QiKey key : intersection) {
            Object medR = medUniqueByKey.get(key);
            WorkData workR = workUniqueByKey.get(key);
            String candName = workR.getFName() + " " + workR.getLName();

            String age;
            String gender;
            String postal;
            Long id;
            if (medR instanceof MedDataDeid m) {
                age = "" + m.getAge();
                gender = m.getGender();
                postal = m.getPostalCode();
                id = m.getId();
            } else {
                MedDataKAnon m = (MedDataKAnon) medR;
                age = m.getAge();
                gender = m.getGender();
                postal = m.getPostalCode();
                id = m.getId();
            }

            linked.add(new LinkedRow(id, age, gender, postal, candName));
        }


        List<MedData> med = medRepo.findAll();
        Map<TripleKey, String> confirmByKey = med.stream()
                .collect(Collectors.toMap(
                        row -> new TripleKey("" + row.getAge(), row.getGender(), row.getPostalCode()),
                        MedData::getName
                ));

        int correct = 0;
        for (LinkedRow L : linked) {
            String name = confirmByKey.get(new TripleKey(L.getAge(), L.getGender(), L.getPostal()));
            L.setTrueName(name);
            boolean ok = name != null && name.equalsIgnoreCase(L.getCandidateName());
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

    // ---- helpers ----
    private static double round1(double v) { return Math.round(v * 10.0) / 10.0; }
    record QiKey(String postal, String gender) { }
    record TripleKey(String age, String gender, String postal) { }
}
