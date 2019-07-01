package com.smalleyes.aws.redis;

import java.util.HashSet;

import com.google.common.base.Preconditions;
import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;

/**
 * 布隆过滤器方法
 * 
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 0.0.1
 *
 */
public class BloomFilterHelper<T> {

	private int numHashFunctions;

	private int bitSize;

	private Funnel<T> funnel;

	public BloomFilterHelper(){}
	
	public BloomFilterHelper(Funnel<T> funnel, int expectedInsertions, double fpp) {
		Preconditions.checkArgument(funnel != null, "funnel不能为空");
		this.funnel = funnel;
		bitSize = optimalNumOfBits(expectedInsertions, fpp);
		numHashFunctions = optimalNumOfHashFunctions(expectedInsertions, bitSize);
	}

	int[] murmurHashOffset(T value) {
		int[] offset = new int[numHashFunctions];
		long hash64 = Hashing.murmur3_128().hashObject(value, funnel).asLong();
		int hash1 = (int) hash64;
		int hash2 = (int) (hash64 >>> 32);
		for (int i = 1; i <= numHashFunctions; i++) {
			int nextHash = hash1 + i * hash2;
			if (nextHash < 0) {
				nextHash = ~nextHash;
			}
			offset[i - 1] = nextHash % bitSize;
		}

		return offset;
	}

	/**
	 * 计算bit数组长度
	 */
	public int optimalNumOfBits(long n, double p) {
		if (p == 0) {
			p = Double.MIN_VALUE;
		}
		return (int) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
	}

	/**
	 * 计算hash方法执行次数
	 */
	public int optimalNumOfHashFunctions(long n, long m) {
		return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
	}

	
	
	public long[] getHash(String word) {
		HashSet<Long> totalSet = new HashSet<>();
		for (int seed : seeds) {
			int hash = hash(word, seed);
			totalSet.add((long) hash);
		}
		long[] offsets = new long[totalSet.size()];
		int i = 0;
		for (Long l : totalSet) {
			offsets[i++] = l;
		}
		return offsets;
	}

	// 总的bitmap大小 64M
	private static final int cap = 1 << 29;
	/*
	 * 不同哈希函数的种子，一般取质数 seeds数组共有8个值，则代表采用8种不同的哈希函数
	 */
	private int[] seeds = new int[] { 3, 5, 7, 11, 13, 31, 37, 61 };

	private int hash(String value, int seed) {
		int result = 0;
		int length = value.length();
		for (int i = 0; i < length; i++) {
			result = seed * result + value.charAt(i);
		}
		return (cap - 1) & result;
	}

}