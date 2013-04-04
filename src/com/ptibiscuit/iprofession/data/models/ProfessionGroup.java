/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data.models;

import java.util.ArrayList;

public class ProfessionGroup {
    private ArrayList<Profession> professions = new ArrayList<Profession>();
    private int maxProfessionsPerPlayer = 1;

    public ProfessionGroup(ArrayList<Profession> listp, int max) {
        this.professions = listp;
        this.maxProfessionsPerPlayer = max;
    }

    public int getContains(ArrayList<Profession> list) {
        int c = 0;
        for (Profession p : list) {
            if (this.professions.contains(p)) {
                c++;
            }
        }
        return c;
    }

    public int getMaxProfessionsPerPlayer() {
        return maxProfessionsPerPlayer;
    }

    public boolean isInGroup(Profession p) {
        return (this.professions.contains(p));
    }

    public ArrayList<Profession> getProfessions() {
        return professions;
    }

}
