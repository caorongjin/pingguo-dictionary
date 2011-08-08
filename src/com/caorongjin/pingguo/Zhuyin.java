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

import java.util.HashMap;
import java.util.Map;

public class Zhuyin {

	public static String format(String pinyin) {
		String[] segments = pinyin.split("\\p{Space}");

		StringBuilder sb = new StringBuilder();

		try {
			for (String segment : segments) {
				if (_needFormatting(segment)) {
					sb.append(_format(segment) + _SPACE);
				}
				else {
					sb.append(segment + _SPACE);
				}
			}
		}
		catch (Exception e) {
			// Cannot convert to zhuyin.  Use pinyin instead.

			return pinyin;
		}

		String zhuyin = sb.toString().trim();

		if (zhuyin.endsWith(_SPACE)) {
			zhuyin = zhuyin.substring(0, zhuyin.length() - _SPACE.length());
		}

		return zhuyin;
	}

	private static String _format(String pinyin) throws Exception {
		int tone = 5;
		if (pinyin.matches("(.*)[1-5]")) {
			tone = Integer.parseInt(pinyin.substring(pinyin.length() - 1));
			pinyin = pinyin.substring(0, pinyin.length() - 1);
		}

		if (!_py2zy.containsKey(pinyin)) {
			throw new Exception();
		}

		return _py2zy.get(pinyin) + _TONES[tone-1];
	}

	private static boolean _needFormatting(String segment) {
		boolean skip = true;

		if (segment.matches("\\p{Punct}")) {
			skip = false;
		}
		else if (segment.matches(_MIDDLE_DOT)) {
			skip = false;
		}

		return skip;
	}

	private static final String _MIDDLE_DOT = "\u00b7";

	private static final String _SPACE = "\u3000";

	private static final String[] _TONES = {
		"", "\u02ca", "\u02C7", "\u02CB", "\u30fb"
	};

	private static final Map<String, String> _py2zy =
		new HashMap<String, String>();

	static {
		// Non-standard mappings

		_py2zy.put("dia", "\u3109\u3127\u311A");
		_py2zy.put("kei", "\u310E\u311F");
		_py2zy.put("o", "\u311B");
		_py2zy.put("r", "\u3126");
		_py2zy.put("yo", "\u3127\u3121");

		// Mappings from <a href="http://www.pinyin.info/romanization/bopomofo/basic.html">www.pinyin.info</a>

		_py2zy.put("a", "\u311A");
		_py2zy.put("ai", "\u311E");
		_py2zy.put("an", "\u3122");
		_py2zy.put("ang", "\u3124");
		_py2zy.put("ao", "\u3120");
		_py2zy.put("ba", "\u3105\u311A");
		_py2zy.put("bai", "\u3105\u311E");
		_py2zy.put("ban", "\u3105\u3122");
		_py2zy.put("bang", "\u3105\u3124");
		_py2zy.put("bao", "\u3105\u3120");
		_py2zy.put("bei", "\u3105\u311F");
		_py2zy.put("ben", "\u3105\u3123");
		_py2zy.put("beng", "\u3105\u3125");
		_py2zy.put("bi", "\u3105\u3127");
		_py2zy.put("bian", "\u3105\u3127\u3122");
		_py2zy.put("biao", "\u3105\u3127\u3120");
		_py2zy.put("bie", "\u3105\u3127\u311D");
		_py2zy.put("bin", "\u3105\u3127\u3123");
		_py2zy.put("bing", "\u3105\u3127\u3125");
		_py2zy.put("bo", "\u3105\u311B");
		_py2zy.put("bu", "\u3105\u3128");
		_py2zy.put("ca", "\u3118\u311A");
		_py2zy.put("cai", "\u3118\u311E");
		_py2zy.put("can", "\u3118\u3122");
		_py2zy.put("cang", "\u3118\u3124");
		_py2zy.put("cao", "\u3118\u3120");
		_py2zy.put("ce", "\u3118\u311C");
		_py2zy.put("cen", "\u3118\u3123");
		_py2zy.put("ceng", "\u3118\u3125");
		_py2zy.put("cha", "\u3114\u311A");
		_py2zy.put("chai", "\u3114\u311E");
		_py2zy.put("chan", "\u3114\u3122");
		_py2zy.put("chang", "\u3114\u3124");
		_py2zy.put("chao", "\u3114\u3120");
		_py2zy.put("che", "\u3114\u311C");
		_py2zy.put("chen", "\u3114\u3123");
		_py2zy.put("cheng", "\u3114\u3125");
		_py2zy.put("chi", "\u3114");
		_py2zy.put("chong", "\u3114\u3128\u3125");
		_py2zy.put("chou", "\u3114\u3121");
		_py2zy.put("chu", "\u3114\u3128");
		_py2zy.put("chua", "\u3114\u3128\u311A");
		_py2zy.put("chuai", "\u3114\u3128\u311E");
		_py2zy.put("chuan", "\u3114\u3128\u3122");
		_py2zy.put("chuang", "\u3114\u3128\u3124");
		_py2zy.put("chui", "\u3114\u3128\u311F");
		_py2zy.put("chun", "\u3114\u3128\u3123");
		_py2zy.put("chuo", "\u3114\u3128\u311B");
		_py2zy.put("ci", "\u3118");
		_py2zy.put("cong", "\u3118\u3128\u3125");
		_py2zy.put("cou", "\u3118\u3121");
		_py2zy.put("cu", "\u3118\u3128");
		_py2zy.put("cuan", "\u3118\u3128\u3122");
		_py2zy.put("cui", "\u3118\u3128\u311F");
		_py2zy.put("cun", "\u3118\u3128\u3123");
		_py2zy.put("cuo", "\u3118\u3128\u311B");
		_py2zy.put("da", "\u3109\u311A");
		_py2zy.put("dai", "\u3109\u311E");
		_py2zy.put("dan", "\u3109\u3122");
		_py2zy.put("dang", "\u3109\u3124");
		_py2zy.put("dao", "\u3109\u3120");
		_py2zy.put("de", "\u3109\u311C");
		_py2zy.put("dei", "\u3109\u311F");
		_py2zy.put("den", "\u3109\u3123");
		_py2zy.put("deng", "\u3109\u3125");
		_py2zy.put("di", "\u3109\u3127");
		_py2zy.put("dian", "\u3109\u3127\u3122");
		_py2zy.put("diang", "\u3109\u3127\u3124");
		_py2zy.put("diao", "\u3109\u3127\u3120");
		_py2zy.put("die", "\u3109\u3127\u311D");
		_py2zy.put("ding", "\u3109\u3127\u3125");
		_py2zy.put("diu", "\u3109\u3127\u3121");
		_py2zy.put("dong", "\u3109\u3128\u3125");
		_py2zy.put("dou", "\u3109\u3121");
		_py2zy.put("du", "\u3109\u3128");
		_py2zy.put("duan", "\u3109\u3128\u3122");
		_py2zy.put("dui", "\u3109\u3128\u311F");
		_py2zy.put("dun", "\u3109\u3128\u3123");
		_py2zy.put("duo", "\u3109\u3128\u311B");
		_py2zy.put("e", "\u311C");
		_py2zy.put("ei", "\u311F");
		_py2zy.put("en", "\u3123");
		_py2zy.put("er", "\u3126");
		_py2zy.put("fa", "\u3108\u311A");
		_py2zy.put("fan", "\u3108\u3122");
		_py2zy.put("fang", "\u3108\u3124");
		_py2zy.put("fei", "\u3108\u311F");
		_py2zy.put("fen", "\u3108\u3123");
		_py2zy.put("feng", "\u3108\u3125");
		_py2zy.put("fo", "\u3108\u311B");
		_py2zy.put("fou", "\u3108\u3121");
		_py2zy.put("fu", "\u3108\u3128");
		_py2zy.put("ga", "\u310D\u311A");
		_py2zy.put("gai", "\u310D\u311E");
		_py2zy.put("gan", "\u310D\u3122");
		_py2zy.put("gang", "\u310D\u3124");
		_py2zy.put("gao", "\u310D\u3120");
		_py2zy.put("ge", "\u310D\u311C");
		_py2zy.put("gei", "\u310D\u311F");
		_py2zy.put("gen", "\u310D\u3123");
		_py2zy.put("geng", "\u310D\u3125");
		_py2zy.put("gong", "\u310D\u3128\u3125");
		_py2zy.put("gou", "\u310D\u3121");
		_py2zy.put("gu", "\u310D\u3128");
		_py2zy.put("gua", "\u310D\u3128\u311A");
		_py2zy.put("guai", "\u310D\u3128\u311E");
		_py2zy.put("guan", "\u310D\u3128\u3122");
		_py2zy.put("guang", "\u310D\u3128\u3124");
		_py2zy.put("gui", "\u310D\u3128\u311F");
		_py2zy.put("gun", "\u310D\u3128\u3123");
		_py2zy.put("guo", "\u310D\u3128\u311B");
		_py2zy.put("ha", "\u310F\u311A");
		_py2zy.put("hai", "\u310F\u311E");
		_py2zy.put("han", "\u310F\u3122");
		_py2zy.put("hang", "\u310F\u3124");
		_py2zy.put("hao", "\u310F\u3120");
		_py2zy.put("he", "\u310F\u311C");
		_py2zy.put("hei", "\u310F\u311F");
		_py2zy.put("hen", "\u310F\u3123");
		_py2zy.put("heng", "\u310F\u3125");
		_py2zy.put("hong", "\u310F\u3128\u3125");
		_py2zy.put("hou", "\u310F\u3121");
		_py2zy.put("hu", "\u310F\u3128");
		_py2zy.put("hua", "\u310F\u3128\u311A");
		_py2zy.put("huai", "\u310F\u3128\u311E");
		_py2zy.put("huan", "\u310F\u3128\u3122");
		_py2zy.put("huang", "\u310F\u3128\u3124");
		_py2zy.put("hui", "\u310F\u3128\u311F");
		_py2zy.put("hun", "\u310F\u3128\u3123");
		_py2zy.put("huo", "\u310F\u3128\u311B");
		_py2zy.put("ji", "\u3110\u3127");
		_py2zy.put("jia", "\u3110\u3127\u311A");
		_py2zy.put("jian", "\u3110\u3127\u3122");
		_py2zy.put("jiang", "\u3110\u3127\u3124");
		_py2zy.put("jiao", "\u3110\u3127\u3120");
		_py2zy.put("jie", "\u3110\u3127\u311D");
		_py2zy.put("jin", "\u3110\u3127\u3123");
		_py2zy.put("jing", "\u3110\u3127\u3125");
		_py2zy.put("jiong", "\u3110\u3129\u3125");
		_py2zy.put("jiu", "\u3110\u3127\u3121");
		_py2zy.put("ju", "\u3110\u3129");
		_py2zy.put("juan", "\u3110\u3129\u3122");
		_py2zy.put("jue", "\u3110\u3129\u311D");
		_py2zy.put("jun", "\u3110\u3129\u3123");
		_py2zy.put("ka", "\u310E\u311A");
		_py2zy.put("kai", "\u310E\u311E");
		_py2zy.put("kan", "\u310E\u3122");
		_py2zy.put("kang", "\u310E\u3124");
		_py2zy.put("kao", "\u310E\u3120");
		_py2zy.put("ke", "\u310E\u311C");
		_py2zy.put("ken", "\u310E\u3123");
		_py2zy.put("keng", "\u310E\u3125");
		_py2zy.put("kong", "\u310E\u3128\u3125");
		_py2zy.put("kou", "\u310E\u3121");
		_py2zy.put("ku", "\u310E\u3128");
		_py2zy.put("kua", "\u310E\u3128\u311A");
		_py2zy.put("kuai", "\u310E\u3128\u311E");
		_py2zy.put("kuan", "\u310E\u3128\u3122");
		_py2zy.put("kuang", "\u310E\u3128\u3124");
		_py2zy.put("kui", "\u310E\u3128\u311F");
		_py2zy.put("kun", "\u310E\u3128\u3123");
		_py2zy.put("kuo", "\u310E\u3128\u311B");
		_py2zy.put("la", "\u310C\u311A");
		_py2zy.put("lai", "\u310C\u311E");
		_py2zy.put("lan", "\u310C\u3122");
		_py2zy.put("lang", "\u310C\u3124");
		_py2zy.put("lao", "\u310C\u3120");
		_py2zy.put("le", "\u310C\u311C");
		_py2zy.put("lei", "\u310C\u311F");
		_py2zy.put("leng", "\u310C\u3125");
		_py2zy.put("li", "\u310C\u3127");
		_py2zy.put("lia", "\u310C\u3127\u311A");
		_py2zy.put("lian", "\u310C\u3127\u3122");
		_py2zy.put("liang", "\u310C\u3127\u3124");
		_py2zy.put("liao", "\u310C\u3127\u3120");
		_py2zy.put("lie", "\u310C\u3127\u311D");
		_py2zy.put("lin", "\u310C\u3127\u3123");
		_py2zy.put("ling", "\u310C\u3127\u3125");
		_py2zy.put("liu", "\u310C\u3127\u3121");
		_py2zy.put("lo", "\u310C\u311B");
		_py2zy.put("long", "\u310C\u3128\u3125");
		_py2zy.put("lou", "\u310C\u3121");
		_py2zy.put("lu", "\u310C\u3128");
		_py2zy.put("lu:", "\u310C\u3129");
		_py2zy.put("lu:e", "\u310C\u3129\u311D");
		_py2zy.put("lu:n", "\u310C\u3129\u3123");
		_py2zy.put("luan", "\u310C\u3128\u3122");
		_py2zy.put("lun", "\u310C\u3128\u3123");
		_py2zy.put("luo", "\u310C\u3128\u311B");
		_py2zy.put("ma", "\u3107\u311A");
		_py2zy.put("mai", "\u3107\u311E");
		_py2zy.put("man", "\u3107\u3122");
		_py2zy.put("mang", "\u3107\u3124");
		_py2zy.put("mao", "\u3107\u3120");
		_py2zy.put("me", "\u3107\u311C");
		_py2zy.put("mei", "\u3107\u311F");
		_py2zy.put("men", "\u3107\u3123");
		_py2zy.put("meng", "\u3107\u3125");
		_py2zy.put("mi", "\u3107\u3127");
		_py2zy.put("mian", "\u3107\u3127\u3122");
		_py2zy.put("miao", "\u3107\u3127\u3120");
		_py2zy.put("mie", "\u3107\u3127\u311D");
		_py2zy.put("min", "\u3107\u3127\u3123");
		_py2zy.put("ming", "\u3107\u3127\u3125");
		_py2zy.put("miu", "\u3107\u3127\u3121");
		_py2zy.put("mo", "\u3107\u311B");
		_py2zy.put("mou", "\u3107\u3121");
		_py2zy.put("mu", "\u3107\u3128");
		_py2zy.put("na", "\u310B\u311A");
		_py2zy.put("nai", "\u310B\u311E");
		_py2zy.put("nan", "\u310B\u3122");
		_py2zy.put("nang", "\u310B\u3124");
		_py2zy.put("nao", "\u310B\u3120");
		_py2zy.put("ne", "\u310B\u311C");
		_py2zy.put("nei", "\u310B\u311F");
		_py2zy.put("nen", "\u310B\u3123");
		_py2zy.put("neng", "\u310B\u3125");
		_py2zy.put("ni", "\u310B\u3127");
		_py2zy.put("nia", "\u310B\u3127\u311A");
		_py2zy.put("nian", "\u310B\u3127\u3122");
		_py2zy.put("niang", "\u310B\u3127\u3124");
		_py2zy.put("niao", "\u310B\u3127\u3120");
		_py2zy.put("nie", "\u310B\u3127\u311D");
		_py2zy.put("nin", "\u310B\u3127\u3123");
		_py2zy.put("ning", "\u310B\u3127\u3125");
		_py2zy.put("niu", "\u310B\u3127\u3121");
		_py2zy.put("nong", "\u310B\u3128\u3125");
		_py2zy.put("nou", "\u310B\u3121");
		_py2zy.put("nu", "\u310B\u3128");
		_py2zy.put("nu:", "\u310B\u3129");
		_py2zy.put("nu:e", "\u310B\u3129\u311D");
		_py2zy.put("nuan", "\u310B\u3128\u3122");
		_py2zy.put("nun", "\u310B\u3128\u3123");
		_py2zy.put("nuo", "\u310B\u3128\u311B");
		_py2zy.put("ou", "\u3121");
		_py2zy.put("pa", "\u3106\u311A");
		_py2zy.put("pai", "\u3106\u311E");
		_py2zy.put("pan", "\u3106\u3122");
		_py2zy.put("pang", "\u3106\u3124");
		_py2zy.put("pao", "\u3106\u3120");
		_py2zy.put("pei", "\u3106\u311F");
		_py2zy.put("pen", "\u3106\u3123");
		_py2zy.put("peng", "\u3106\u3125");
		_py2zy.put("pi", "\u3106\u3127");
		_py2zy.put("pian", "\u3106\u3127\u3122");
		_py2zy.put("piao", "\u3106\u3127\u3120");
		_py2zy.put("pie", "\u3106\u3127\u311D");
		_py2zy.put("pin", "\u3106\u3127\u3123");
		_py2zy.put("ping", "\u3106\u3127\u3125");
		_py2zy.put("po", "\u3106\u311B");
		_py2zy.put("pou", "\u3106\u3121");
		_py2zy.put("pu", "\u3106\u3128");
		_py2zy.put("qi", "\u3111\u3127");
		_py2zy.put("qia", "\u3111\u3127\u311A");
		_py2zy.put("qian", "\u3111\u3127\u3122");
		_py2zy.put("qiang", "\u3111\u3127\u3124");
		_py2zy.put("qiao", "\u3111\u3127\u3120");
		_py2zy.put("qie", "\u3111\u3127\u311D");
		_py2zy.put("qin", "\u3111\u3127\u3123");
		_py2zy.put("qing", "\u3111\u3127\u3125");
		_py2zy.put("qiong", "\u3111\u3129\u3125");
		_py2zy.put("qiu", "\u3111\u3127\u3121");
		_py2zy.put("qu", "\u3111\u3129");
		_py2zy.put("quan", "\u3111\u3129\u3122");
		_py2zy.put("que", "\u3111\u3129\u311D");
		_py2zy.put("qun", "\u3111\u3129\u3123");
		_py2zy.put("ran", "\u3116\u3122");
		_py2zy.put("rang", "\u3116\u3124");
		_py2zy.put("rao", "\u3116\u3120");
		_py2zy.put("re", "\u3116\u311C");
		_py2zy.put("ren", "\u3116\u3123");
		_py2zy.put("reng", "\u3116\u3125");
		_py2zy.put("ri", "\u3116");
		_py2zy.put("rong", "\u3116\u3128\u3125");
		_py2zy.put("rou", "\u3116\u3121");
		_py2zy.put("ru", "\u3116\u3128");
		_py2zy.put("ruan", "\u3116\u3128\u3122");
		_py2zy.put("rui", "\u3116\u3128\u311F");
		_py2zy.put("run", "\u3116\u3128\u3123");
		_py2zy.put("ruo", "\u3116\u3128\u311B");
		_py2zy.put("sa", "\u3119\u311A");
		_py2zy.put("sai", "\u3119\u311E");
		_py2zy.put("san", "\u3119\u3122");
		_py2zy.put("sang", "\u3119\u3124");
		_py2zy.put("sao", "\u3119\u3120");
		_py2zy.put("se", "\u3119\u311C");
		_py2zy.put("sei", "\u3119\u311F");
		_py2zy.put("sen", "\u3119\u3123");
		_py2zy.put("seng", "\u3119\u3125");
		_py2zy.put("sha", "\u3115\u311A");
		_py2zy.put("shai", "\u3115\u311E");
		_py2zy.put("shan", "\u3115\u3122");
		_py2zy.put("shang", "\u3115\u3124");
		_py2zy.put("shao", "\u3115\u3120");
		_py2zy.put("she", "\u3115\u311C");
		_py2zy.put("shei", "\u3115\u311F");
		_py2zy.put("shen", "\u3115\u3123");
		_py2zy.put("sheng", "\u3115\u3125");
		_py2zy.put("shi", "\u3115");
		_py2zy.put("shong", "\u3115\u3128\u3125");
		_py2zy.put("shou", "\u3115\u3121");
		_py2zy.put("shu", "\u3115\u3128");
		_py2zy.put("shua", "\u3115\u3128\u311A");
		_py2zy.put("shuai", "\u3115\u3128\u311E");
		_py2zy.put("shuan", "\u3115\u3128\u3122");
		_py2zy.put("shuang", "\u3115\u3128\u3124");
		_py2zy.put("shui", "\u3115\u3128\u311F");
		_py2zy.put("shun", "\u3115\u3128\u3123");
		_py2zy.put("shuo", "\u3115\u3128\u311B");
		_py2zy.put("si", "\u3119");
		_py2zy.put("song", "\u3119\u3128\u3125");
		_py2zy.put("sou", "\u3119\u3121");
		_py2zy.put("su", "\u3119\u3128");
		_py2zy.put("suan", "\u3119\u3128\u3122");
		_py2zy.put("sui", "\u3119\u3128\u311F");
		_py2zy.put("sun", "\u3119\u3128\u3123");
		_py2zy.put("suo", "\u3119\u3128\u311B");
		_py2zy.put("ta", "\u310A\u311A");
		_py2zy.put("tai", "\u310A\u311E");
		_py2zy.put("tan", "\u310A\u3122");
		_py2zy.put("tang", "\u310A\u3124");
		_py2zy.put("tao", "\u310A\u3120");
		_py2zy.put("te", "\u310A\u311C");
		_py2zy.put("teng", "\u310A\u3125");
		_py2zy.put("ti", "\u310A\u3127");
		_py2zy.put("tian", "\u310A\u3127\u3122");
		_py2zy.put("tiao", "\u310A\u3127\u3120");
		_py2zy.put("tie", "\u310A\u3127\u311D");
		_py2zy.put("ting", "\u310A\u3127\u3125");
		_py2zy.put("tong", "\u310A\u3128\u3125");
		_py2zy.put("tou", "\u310A\u3121");
		_py2zy.put("tu", "\u310A\u3128");
		_py2zy.put("tuan", "\u310A\u3128\u3122");
		_py2zy.put("tui", "\u310A\u3128\u311F");
		_py2zy.put("tun", "\u310A\u3128\u3123");
		_py2zy.put("tuo", "\u310A\u3128\u311B");
		_py2zy.put("wa", "\u3128\u311A");
		_py2zy.put("wai", "\u3128\u311E");
		_py2zy.put("wan", "\u3128\u3122");
		_py2zy.put("wang", "\u3128\u3124");
		_py2zy.put("wei", "\u3128\u311F");
		_py2zy.put("wen", "\u3128\u3123");
		_py2zy.put("weng", "\u3128\u3125");
		_py2zy.put("wo", "\u3128\u311B");
		_py2zy.put("wu", "\u3128");
		_py2zy.put("xi", "\u3112\u3127");
		_py2zy.put("xia", "\u3112\u3127\u311A");
		_py2zy.put("xian", "\u3112\u3127\u3122");
		_py2zy.put("xiang", "\u3112\u3127\u3124");
		_py2zy.put("xiao", "\u3112\u3127\u3120");
		_py2zy.put("xie", "\u3112\u3127\u311D");
		_py2zy.put("xin", "\u3112\u3127\u3123");
		_py2zy.put("xing", "\u3112\u3127\u3125");
		_py2zy.put("xiong", "\u3112\u3129\u3125");
		_py2zy.put("xiu", "\u3112\u3127\u3121");
		_py2zy.put("xu", "\u3112\u3129");
		_py2zy.put("xuan", "\u3112\u3129\u3122");
		_py2zy.put("xue", "\u3112\u3129\u311D");
		_py2zy.put("xun", "\u3112\u3129\u3123");
		_py2zy.put("ya", "\u3127\u311A");
		_py2zy.put("yan", "\u3127\u3122");
		_py2zy.put("yang", "\u3127\u3124");
		_py2zy.put("yao", "\u3127\u3120");
		_py2zy.put("ye", "\u3127\u311D");
		_py2zy.put("yi", "\u3127");
		_py2zy.put("yin", "\u3127\u3123");
		_py2zy.put("ying", "\u3127\u3125");
		_py2zy.put("yong", "\u3129\u3125");
		_py2zy.put("you", "\u3127\u3121");
		_py2zy.put("yu", "\u3129");
		_py2zy.put("yuan", "\u3129\u3122");
		_py2zy.put("yue", "\u3129\u311D");
		_py2zy.put("yun", "\u3129\u3123");
		_py2zy.put("za", "\u3117\u311A");
		_py2zy.put("zai", "\u3117\u311E");
		_py2zy.put("zan", "\u3117\u3122");
		_py2zy.put("zang", "\u3117\u3124");
		_py2zy.put("zao", "\u3117\u3120");
		_py2zy.put("ze", "\u3117\u311C");
		_py2zy.put("zei", "\u3117\u311F");
		_py2zy.put("zen", "\u3117\u3123");
		_py2zy.put("zeng", "\u3117\u3125");
		_py2zy.put("zha", "\u3113\u311A");
		_py2zy.put("zhai", "\u3113\u311E");
		_py2zy.put("zhan", "\u3113\u3122");
		_py2zy.put("zhang", "\u3113\u3124");
		_py2zy.put("zhao", "\u3113\u3120");
		_py2zy.put("zhe", "\u3113\u311C");
		_py2zy.put("zhei", "\u3113\u311F");
		_py2zy.put("zhen", "\u3113\u3123");
		_py2zy.put("zheng", "\u3113\u3125");
		_py2zy.put("zhi", "\u3113");
		_py2zy.put("zhong", "\u3113\u3128\u3125");
		_py2zy.put("zhou", "\u3113\u3121");
		_py2zy.put("zhu", "\u3113\u3128");
		_py2zy.put("zhua", "\u3113\u3128\u311A");
		_py2zy.put("zhuai", "\u3113\u3128\u311E");
		_py2zy.put("zhuan", "\u3113\u3128\u3122");
		_py2zy.put("zhuang", "\u3113\u3128\u3124");
		_py2zy.put("zhui", "\u3113\u3128\u311F");
		_py2zy.put("zhun", "\u3113\u3128\u3123");
		_py2zy.put("zhuo", "\u3113\u3128\u311B");
		_py2zy.put("zi", "\u3117");
		_py2zy.put("zong", "\u3117\u3128\u3125");
		_py2zy.put("zou", "\u3117\u3121");
		_py2zy.put("zu", "\u3117\u3128");
		_py2zy.put("zuan", "\u3117\u3128\u3122");
		_py2zy.put("zui", "\u3117\u3128\u311F");
		_py2zy.put("zun", "\u3117\u3128\u3123");
		_py2zy.put("zuo", "\u3117\u3128\u311B");
	}

}