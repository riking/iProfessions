package com.ptibiscuit.iprofession.data.models;

import com.ptibiscuit.iprofession.data.models.TypeSkill;

public class Skill {
	private int id;
	private int metaData;
	private TypeSkill type;
	private String notHave;

	public int getMetaData() {
		return metaData;
	}
	
	public Skill(int id, int metaData, TypeSkill t)
	{
		this.id = id;
		this.metaData = metaData;
		this.type = t;
	}

	public int getId() {
		return id;
	}

	public boolean equals(Skill s)
	{
		return (s.getType() == this.getType() && s.getMetaData() == this.metaData && s.getId() == this.getId());
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
