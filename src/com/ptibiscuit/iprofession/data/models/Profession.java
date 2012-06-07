package com.ptibiscuit.iprofession.data.models;

import java.util.ArrayList;

public class Profession {
	private String name;
	private String tag;
	private ArrayList<Skill> skills = new ArrayList<Skill>();
	private int price;
	private Profession parent;
	
	private ArrayList<Require> prerequis = new ArrayList<Require>();
	
	public Profession(String tag, String name, ArrayList<Skill> skills, ArrayList<Require> prerequis, Profession parent, int price)
	{
		this.name = name;
		this.skills = skills;
		this.tag = tag;
		this.price = price;
		this.prerequis = prerequis;
		this.parent = parent;
	}
	
	public boolean hasSkill(Skill sk)
	{
		// Si ca passe, cela veut dire que notre skill avait une particularité. On va donc en créer un global
		Skill globalSkill = null;
		if (sk.getMetaData() != -1)
			globalSkill = new Skill(sk.getId(), -1, sk.getType());
		for (Skill s : skills)
		{
			if (sk.equals(s))
				return true;
			// On regarde si cette compétence ne serait pas la même, mais en global
			if (globalSkill != null && s.equals(globalSkill))
				return true;
		}
		
		// Ok, on va tenter notre profession supérieure, si on en a une.
		if (parent != null)
			return this.parent.hasSkill(sk);
		return false;
	}
	
	public String getName() {
		return name;
	}

	public String getTag() {
		return tag;
	}
	
	public boolean isInTheSameTree(Profession other) {
		// On regarde si en faisant l'iteration des parents de l'un des 2, on tombe sur l'autre.
		Profession localParent = other;
		while (localParent != null) {
			if (localParent == this)
				return true;
			localParent = localParent.getParent();
		}
		
		// Maintenant, en partant de this
		localParent = this.getParent();
		while (localParent != null) {
			if (localParent == other)
				return true;
			localParent = localParent.getParent();
		}
		
		return false;
	}
	
	public ArrayList<Skill> getSkills() {
		return skills;
	}

	public ArrayList<Require> getRequired() {
		return prerequis;
	}

	public int getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return tag;
	}

	public Profession getParent() {
		return parent;
	}
}
