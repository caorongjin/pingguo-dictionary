/**
 * Copyright (c) 2008-2011 Alexander Chow
 *
 * Pingguo Dictionary is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the 
 * Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 *
 * Pingguo Dictionary is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License 
 * for more details.
 */
package com.caorongjin.pingguo;

public class Pinyin {

	public static String format(String pinyin) {
		String[] segments = pinyin.split("\\p{Space}");

		StringBuilder sb = new StringBuilder();
		for (String segment : segments) {
			sb.append(_format(segment) + " ");
		}

		return sb.toString().trim();
	}

	private static String _format(String pinyin) {
		// 1. Look for an "a" or an "e". If either vowel appears, it takes the
		// tone mark. There are no possible pinyin syllables that contain both
		// an "a" and an "e".
		// 2. If there is no "a" or "e", look for an "ou". If "ou" appears, then
		// the "o" takes the tone mark.
		// 3. If none of the above cases hold, then the last vowel in the
		// syllable takes the tone mark.

		if (pinyin.matches("(.*)[1-5]")) {
			int tone = Integer.parseInt(pinyin.substring(pinyin.length() - 1));
			pinyin = pinyin.substring(0, pinyin.length() - 1);

			if (tone != 5) {
				if (pinyin.matches("(.*)[ae](.*)")) {
					pinyin = pinyin.replaceFirst("a", _PINYIN_A[tone-1]);
					pinyin = pinyin.replaceFirst("e", _PINYIN_E[tone-1]);
				}
				else if (pinyin.matches("(.*)ou(.*)")) {
					pinyin = pinyin.replaceFirst("o", _PINYIN_O[tone-1]);
				}
				else {
					int length = pinyin.length();

					for (int i = length; i > 0; i--) {
						String sub = pinyin.substring(i - 1);

						if (sub.matches("(.*)[iou](.*)")) {
							sub = sub.replaceFirst("i", _PINYIN_I[tone-1]);
							sub = sub.replaceFirst("o", _PINYIN_O[tone-1]);
							sub = sub.replaceFirst("u:", _PINYIN_V[tone-1]);
							sub = sub.replaceFirst("u", _PINYIN_U[tone-1]);

							pinyin = pinyin.substring(0, i - 1) + sub;

							break;
						}
					}
				}
			}
		}

		pinyin = pinyin.replace("u:", "\u00fc");

		return pinyin;
	}

	private static final String[] _PINYIN_A = {
			"\u0101", "\u00e1", "\u01ce", "\u00e0"
		};

	private static final String[] _PINYIN_E = {
			"\u0113", "\u00e9", "\u011b", "\u00e8"
		};

	private static final String[] _PINYIN_I = {
			"\u012b", "\u00ed", "\u01d0", "\u00ec"
		};

	private static final String[] _PINYIN_O = {
			"\u014d", "\u00f3", "\u01d2", "\u00f2"
		};

	private static final String[] _PINYIN_U = {
			"\u016b", "\u00fa", "\u01d4", "\u00f9"
		};

	private static final String[] _PINYIN_V = {
			"\u01d6", "\u01d8", "\u01da", "\u01dc"
		};

}