package com.radar.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class NumberUtil {
	private static final Logger logger = LoggerFactory.getLogger(NumberUtil.class);

	public static int findMax(int ... nums) {
		return max(nums);
	}

	public static int findMin(int ... nums) {
		return min(nums);
	}

	public static int max(int n[]) {
		int max = n[0];

		for (int i = 1; i < n.length; i++)
			if (n[i] > max) max = n[i];

		return max;
	}

	public static int min(int n[]) {
		int min = n[0];

		for (int i = 1; i < n.length; i++)
			if (n[i] < min) min = n[i];

		return min;
	}

	public static float findMax(float ... nums) {
		return max(nums);
	}

	public static float findMin(float ... nums) {
		return min(nums);
	}

	public static float max(float n[]) {
		float max = n[0];

		for (int i = 1; i < n.length; i++)
			if (n[i] > max) max = n[i];

		return max;
	}

	public static float min(float n[]) {
		float min = n[0];

		for (int i = 1; i < n.length; i++)
			if (n[i] < min) min = n[i];

		return min;
	}

	public static int ran(int size) {
		Random rnd = new Random();
		return rnd.nextInt(size);
	}
}
