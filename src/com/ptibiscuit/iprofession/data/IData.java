/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptibiscuit.iprofession.data;

import com.ptibiscuit.iprofession.data.models.Profession;
import java.util.ArrayList;
import java.util.HashMap;

public interface IData {
	public void loadProfessions();
	public void loadPlayersProfessions();
	public ArrayList<Profession> getProfessions();
	public Profession getProfession(String tag);
	public Profession getProfessionByPlayer(String player);
	public HashMap<String, Profession> getProfessionPlayers();
	public void setPlayerProfession(String player, Profession profession);
	public void save();
	
}
