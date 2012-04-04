package com.ptibiscuit.iprofession.data.models;

import java.util.HashMap;
import java.util.Map;

public enum TypeSkill {
	CRAFT(1),
	USE(2),
	BREAK(3),
	SMELT(4);
	
	private final static Map<Integer, TypeSkill> types = new HashMap<Integer, TypeSkill>();
	private final int code;
	
	private TypeSkill(int i)
	{
		this.code = i;
	}
	
	public int getCode()
	{
		return code;
	}
	
	public TypeSkill getTypeSkill(int i)
	{
		return types.get(i);
	}
	
	static
	{
		for (TypeSkill color : TypeSkill.values())
		{
			types.put(color.getCode(), color);
		}
	}
}
