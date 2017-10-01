package com.msx.linker;

public interface Scrollable<T>
{
	public abstract T next();
	
	public abstract T prev();
	
	public abstract T current();
}
