package com.smalleyes.aws.common;

import java.util.List;

public interface IBatchExecutor<T> {

	void execute(List<T> records);
}
