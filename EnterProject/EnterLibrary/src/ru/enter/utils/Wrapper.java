package ru.enter.utils;

import java.util.List;

import ru.enter.beans.ErrorBean;

public class Wrapper <T> {

	private List<T> result;
	private ErrorBean error;
	
	public boolean hasError(){
		return error == null;
	}

	public List<T> getResult () {
		return result;
	}

	public void setResult (List<T> result) {
		this.result = result;
	}

	public ErrorBean getError () {
		return error;
	}

	public void setError (ErrorBean error) {
		this.error = error;
	}
}
