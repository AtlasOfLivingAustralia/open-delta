package au.org.ala.delta.intkey.model;

import java.util.Comparator;

public class CharacterComparator implements Comparator<au.org.ala.delta.model.Character> {

    @Override
    public int compare(au.org.ala.delta.model.Character c1, au.org.ala.delta.model.Character c2) {
        return Integer.valueOf(c1.getCharacterId()).compareTo(Integer.valueOf(c2.getCharacterId()));
    }
}
