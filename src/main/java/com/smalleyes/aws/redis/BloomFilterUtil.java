package com.smalleyes.aws.redis;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class BloomFilterUtil<E> {

	// bit总大小
	private int sizeOfBit;
	// 预计添加元素数量
	private int sizeOfElement;
	// hash函数数量
	private int sizeOfHashFunction;

	@Autowired
	private RedisTemplate<String, E> redisTemplate;
	
//	public BloomFilterHelper(int ){
//		
//	}
	

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

	
	public static void main(String[] args) {
		
		System.out.println(1<<29);
		System.out.println(1<<28);
		System.out.println(1<<27);
		System.out.println(1<<26);
		System.out.println(1<<25);
		System.out.println(1<<24);
		System.out.println(1<<23);
		System.out.println(1<<22);
		System.out.println(1<<21);
		System.out.println(1<<20);
		
		
		
		int expectedNumberOfElements = 1000000;
		double falsePositiveProbability = 0.1;
		
		BloomFilterHelper<String> filterHelper = new BloomFilterHelper<>();
		int ss = filterHelper.optimalNumOfBits(expectedNumberOfElements,falsePositiveProbability);
		int cc = filterHelper.optimalNumOfHashFunctions(expectedNumberOfElements, ss);
		System.out.println(ss);
		System.out.println(cc);
		
		// m = ceil(kn/ln2)
		int dd = (int) Math.ceil((int) Math.ceil(-(Math.log(falsePositiveProbability) / Math.log(2))) * expectedNumberOfElements / Math.log(2));
		
		// k = ceil(-ln(f)/ln2)
		int ff = (int) Math.ceil(-(Math.log(falsePositiveProbability) / Math.log(2)));
		
		
		dd = 5770781;
		System.out.println(dd);
		System.out.println(ff);
	}
}
