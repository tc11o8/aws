package com.smalleyes.aws.common;

import java.util.List;

public interface ExceptionListener<T> {
	void onException(List<T> records);
}
