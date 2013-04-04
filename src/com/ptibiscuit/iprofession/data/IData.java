/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.ptibiscuit.iprofession.data;

import com.ptibiscuit.iprofession.data.models.Profession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface IData {
    public void loadProfessions();

    public void loadPlayersProfessions();

    public ArrayList<Profession> getProfessions();

    public Profession getProfession(String tag);

    public ArrayList<Profession> getProfessionByPlayer(String player);

    public HashMap<String, ArrayList<Profession>> getProfessionPlayers();

    public void setPlayerProfession(String player, ArrayList<Profession> profession);

    public void save();

    public void loadActivatedWorlds();

    public List<String> getActivatedWorlds();

}
