package com.ptibiscuit.iprofession.data.models;

import com.ptibiscuit.iprofession.data.models.TypeSkill;

public class Skill {
	private int id;
	private TypeSkill type;
	private String notHave;
	
	public Skill(int id, TypeSkill t)
	{
		this.id = id;
		this.type = t;
	}

	public int getId() {
		return id;
	}

	public boolean equals(Skill s)
	{
		if (s.getType() == this.getType() && s.getId() == this.getId())
			return true;
			
		return false;
	}
	
	public TypeSkill getType() {
		return type;
	}

	public String getNotHave() {
		return notHave;
	}

	public void setNotHave(String notHave) {
		this.notHave = notHave;
	}
}
