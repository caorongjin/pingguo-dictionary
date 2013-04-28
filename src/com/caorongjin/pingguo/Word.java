/**
 * Copyright (c) 2008-2013 Alexander Chow
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

public class Word {

	public void append(Word word) {
		String[] defs = word._defs;

		String[] newDefs = new String[_defs.length + defs.length];

		for (int i = 0; i < _defs.length; i++) {
			newDefs[i] = _defs[i];
		}

		for (int i = 0; i < defs.length; i++) {
			newDefs[i + _defs.length] = defs[i];
		}

		_defs = newDefs;
	}

	public void parse(String line) {
		int x = line.indexOf("[");
		int y = line.indexOf("]");

		String chinese = line.substring(0, x);
		String defs = line.substring(y + 1).trim();

		_pinyin = line.substring(x + 1, y).trim().toLowerCase();

		int clen = chinese.length();
		_trad = chinese.substring(0, clen / 2).trim();
		_simp = chinese.substring(clen / 2).trim();

		defs = defs.substring(1, defs.length() - 1).trim();
		_defs = defs.split("\\p{Space}*/+");
	}

	public String getTrad() {
		return _trad;
	}

	public String getSimp() {
		return _simp;
	}

	public String getPinyin() {
		return _pinyin;
	}

	public String getPlainPinyin() {
		return _pinyin.replaceAll("\\p{Digit}|:", "");
	}

	public String[] getDefs() {
		return _defs;
	}

	public String getTitle(boolean pinyin) {
		StringBuilder sb = new StringBuilder();

		sb.append(_trad + " / " + _simp + " (");
		sb.append(pinyin ?  Pinyin.format(_pinyin) : Zhuyin.format(_pinyin));
		sb.append(")");

		return sb.toString();
	}

	public static String escape(String str) {
		if (str.startsWith("\"")) {
			str = "\\\"" + str.substring(1);
		}

		if (str.endsWith("\"")) {
			str = str.substring(0, str.length() - 1) + "\\\"";
		}

		str = str.trim().replaceAll("<", "&lt;");

		return str;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("\tT: " + _trad + ", S: " + _simp + ", P: " + Pinyin.format(_pinyin) + "\n");

		for (String def : _defs) {
			sb.append("\t\t" + def + "\n");
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		// 中國 中国 [Zhong1 guo2] /China/Middle Kingdom/
		// 紅男綠女 红男绿女 [hong2 nan2 lu:4 nu:3] /young people decked out in gorgeous clothes (成语 saw)/

		String[] lines = {
			"\u4e2d\u570b \u4e2d\u56fd [Zhong1 guo2] /China/Middle Kingdom/",
			"\u7d05\u7537\u7da0\u5973 \u7ea2\u7537\u7eff\u5973 [hong2 nan2 lu:4 nu:3] /young people decked out in gorgeous clothes (\u6210\u8bed saw)/"
		};

		Word word = new Word();

		for (String line : lines) {
			word.parse(line);

			System.out.println("Parse: " + line + "\n");
			System.out.println(word);
		}
	}

	private String _trad;
	private String _simp;
	private String _pinyin;
	private String[] _defs;

}