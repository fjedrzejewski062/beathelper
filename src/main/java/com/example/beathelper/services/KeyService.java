package com.example.beathelper.services;

import com.example.beathelper.entities.Key;
import com.example.beathelper.entities.User;
import com.example.beathelper.enums.KeyType;
import com.example.beathelper.repositories.KeyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class KeyService {
    private final KeyRepository keyRepository;
    private final Random ran = new Random();
    private static final Map<KeyType, String> camelotCircleMap = new HashMap<>();
    private static final Map<String, KeyType> reverseCamelotCircleMap = new HashMap<>();

    static {
        // Major Keys
        camelotCircleMap.put(KeyType.C_MAJOR, "8B");
        camelotCircleMap.put(KeyType.C_SHARP_MAJOR, "3B");
        camelotCircleMap.put(KeyType.D_MAJOR, "10B");
        camelotCircleMap.put(KeyType.D_SHARP_MAJOR, "5B");
        camelotCircleMap.put(KeyType.E_MAJOR, "12B");
        camelotCircleMap.put(KeyType.F_MAJOR, "7B");
        camelotCircleMap.put(KeyType.F_SHARP_MAJOR, "2B");
        camelotCircleMap.put(KeyType.G_MAJOR, "9B");
        camelotCircleMap.put(KeyType.G_SHARP_MAJOR, "4B");
        camelotCircleMap.put(KeyType.A_MAJOR, "11B");
        camelotCircleMap.put(KeyType.A_SHARP_MAJOR, "6B");
        camelotCircleMap.put(KeyType.B_MAJOR, "1B");

        // Minor Keys
        camelotCircleMap.put(KeyType.C_MINOR, "5A");
        camelotCircleMap.put(KeyType.C_SHARP_MINOR, "12A");
        camelotCircleMap.put(KeyType.D_MINOR, "7A");
        camelotCircleMap.put(KeyType.D_SHARP_MINOR, "2A");
        camelotCircleMap.put(KeyType.E_MINOR, "9A");
        camelotCircleMap.put(KeyType.F_MINOR, "4A");
        camelotCircleMap.put(KeyType.F_SHARP_MINOR, "11A");
        camelotCircleMap.put(KeyType.G_MINOR, "6A");
        camelotCircleMap.put(KeyType.G_SHARP_MINOR, "1A");
        camelotCircleMap.put(KeyType.A_MINOR, "8A");
        camelotCircleMap.put(KeyType.A_SHARP_MINOR, "3A");
        camelotCircleMap.put(KeyType.B_MINOR, "10A");

        // Tworzenie mapy odwrotnej
        for (Map.Entry<KeyType, String> entry : camelotCircleMap.entrySet()) {
            reverseCamelotCircleMap.put(entry.getValue(), entry.getKey());
        }
    }



    public KeyService(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    public Key randomKey(User createdBy, String type){
        if (type == null) {
            type = "both";
        }

        List<KeyType> keyTypes;

        switch(type.toLowerCase()) {
            case "major":
                keyTypes = Arrays.stream(KeyType.values())
                        .filter(k -> k.getName().contains("Major"))
                        .toList();
                break;
            case "minor":
                keyTypes = Arrays.stream(KeyType.values())
                        .filter(k -> k.getName().contains("Minor"))
                        .toList();
                break;
            case "both":
                keyTypes = Arrays.asList(KeyType.values());
                break;
            default:
                throw new IllegalArgumentException("Niepoprawny typ: " + type);
        }

        KeyType randomKey = keyTypes.get(ran.nextInt(keyTypes.size()));

        List<KeyType> relatedKeys = findRelatedKeys(randomKey);

        Key key = new Key();
        key.setName(randomKey);
        key.setRelatedKeys(relatedKeys);
        key.setCreatedBy(createdBy);
        key.setCreatedAt(LocalDateTime.now());
        return keyRepository.save(key);
    }

    private List<KeyType> findRelatedKeys(KeyType key){
        List<KeyType> relatedKeys = new ArrayList<>();
        String camelotCode = camelotCircleMap.get(key);

        if (camelotCode == null) {
            return relatedKeys;
        }

        int number = Integer.parseInt(camelotCode.substring(0, camelotCode.length() - 1));
        String letter = camelotCode.substring(camelotCode.length() - 1);

        String oppositeKey = number + (letter.equals("A") ? "B" : "A");
        if (reverseCamelotCircleMap.containsKey(oppositeKey)) {
            relatedKeys.add(reverseCamelotCircleMap.get(oppositeKey));
        }

        String nextKey = ((number % 12) + 1) + letter;
        if (reverseCamelotCircleMap.containsKey(nextKey)) {
            relatedKeys.add(reverseCamelotCircleMap.get(nextKey));
        }

        String prevKey = ((number == 1) ? 12 : number - 1) + letter;
        if (reverseCamelotCircleMap.containsKey(prevKey)) {
            relatedKeys.add(reverseCamelotCircleMap.get(prevKey));
        }

        return relatedKeys;
    }
    public List<Key> findKeysByUser(User user){
        return keyRepository.findByUser(user);
    }
    public Key findById(Long id){
        return keyRepository.findById(id).orElse(null);
    }


    public Key updateKey(Key key){
        return keyRepository.save(key);
    }



    public void deleteKey(Key key){
        keyRepository.delete(key);
    }

    public Optional<Key> findByKey(KeyType key){
        return keyRepository.findByKey(key);
    }
}
