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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.collections.map.MultiValueMap;

public class Pingguo {

	public static void main(String[] args) throws Exception {
		BufferedReader br = null;
		BufferedWriter pyp = null;
		BufferedWriter pyx = null;
		BufferedWriter zyp = null;
		BufferedWriter zyx = null;

		try {
			br = _createReader(args[0]);

			pyp = _createWriter(args[1] + "/" + DICTIONARY_PLIST);
			pyx = _createWriter(args[1] + "/" + DICTIONARY_XML);
			zyp = _createWriter(args[2] + "/" + DICTIONARY_PLIST);
			zyx = _createWriter(args[2] + "/" + DICTIONARY_XML);

			_version = args[3];

			Pingguo pg = new Pingguo(br, pyp, pyx, zyp, zyx);

			pg.generateDictionary();
		}
		finally {
			_cleanup(br);
			_cleanup(pyp);
			_cleanup(pyx);
			_cleanup(zyp);
			_cleanup(zyx);
		}
	}

	private static void _cleanup(BufferedReader br) {
		if (br != null) {
			try {
				br.close();
			}
			catch (Exception e) {
			}
		}
	}

	private static void _cleanup(BufferedWriter bw) {
		if (bw != null) {
			try {
				bw.close();
			}
			catch (Exception e) {
			}
		}
	}

	private static BufferedReader _createReader(String fileName)
		throws Exception {

		InputStreamReader isr =
			new InputStreamReader(new FileInputStream(fileName), "UTF8");

		return new BufferedReader(isr);
	}

	private static BufferedWriter _createWriter(String fileName)
		throws Exception {

		OutputStreamWriter osw =
			new OutputStreamWriter(new FileOutputStream(fileName), "UTF8");

		return new BufferedWriter(osw);
	}

	public Pingguo(
		BufferedReader br, BufferedWriter pyp, BufferedWriter pyx,
		BufferedWriter zyp, BufferedWriter zyx) {

		_br = br;
		_pyp = pyp;
		_pyx = pyx;
		_zyp = zyp;
		_zyx = zyx;

		_mvp = new MultiValueMap();
	}

	public void generateDictionary() throws Exception {
		_writePrefix(_pyx);
		_writePrefix(_zyx);

		Word curr = new Word();
		Word next = new Word();

		String currLine = null;
		String nextLine = null;

		for (;;) {
			if (currLine == null) {
				currLine = _readLine();
			}

			if (nextLine == null) {
				nextLine = _readLine();
			}

			if (currLine == null) {
				break;
			}

			curr.parse(currLine);

			while (nextLine != null) {
				next.parse(nextLine);

				if (curr.getTrad().equals(next.getTrad()) &&
					curr.getSimp().equals(next.getSimp()) &&
					curr.getPinyin().equals(next.getPinyin())) {

					curr.append(next);

					nextLine = _readLine();
				}
				else {
					break;
				}
			}

			_id++;

			_insertEntry(_pyx, curr, true);
			_insertEntry(_zyx, curr, false);

			final String pyTitle = curr.getTitle(true);
			final String zyTitle = curr.getTitle(false);

			for (String def : curr.getDefs()) {
				if (def != null && def.trim().length() > 0) {
					_mvp.put(def, new Object[] { pyTitle, zyTitle, _id });
				}
			}

			currLine = nextLine;
			nextLine = null;
		}

		_buildEnglishIndex(_pyx, true);
		_buildEnglishIndex(_zyx, false);

		_writePostFix(_pyx, true);
		_writePostFix(_zyx, false);

		_writePlist(_pyp, true);
		_writePlist(_zyp, false);
	}

	private void _buildEnglishIndex(BufferedWriter bw, boolean pinyin)
		throws Exception {

		@SuppressWarnings("unchecked")
		Collection<String> defs = _mvp.keySet();
		long index = _id + 1;

		for (String def : defs) {
			bw.write(
				"<d:entry id=\"" + (++index) + "\" d:title=\"" +
				Word.escape(def) + "\">\n");
			_insertIndex(bw, def, def);
			bw.write("<div><h1>" + def + "</h1></div>\n");
			bw.write("<div><ul>\n");

			@SuppressWarnings("unchecked")
			Collection<Object[]> titleIdPairs = _mvp.getCollection(def);

			for (Object[] titleIdPair : titleIdPairs) {
				String title = (String)titleIdPair[pinyin ? 0 : 1];
				long id = (Long)titleIdPair[2];

				bw.write(
					"<li><a href=\"x-dictionary:r:" + id + "\">" + title +
					"</a></li>");
			}

			bw.write("</ul></div>\n");
			bw.write("</d:entry>\n");
		}
	}

	private void _insertEntry(BufferedWriter bw, Word word, boolean pinyin)
		throws Exception {

		String title = word.getTitle(pinyin);

		bw.write(
			"<d:entry id=\"" + _id + "\" d:title=\"" + Word.escape(title) +
			"\">\n");

		// Index

		_insertIndex(bw, word.getTrad(), title);

		if (!word.getTrad().equals(word.getSimp())) {
			_insertIndex(bw, word.getSimp(), title);
		}

		if (pinyin) {
			_insertIndex(bw, word.getPlainPinyin(), title);
			_insertIndex(bw, word.getPinyin(), title);
		}

		// Traditional / Simplified / Phonetic

		boolean priority = false;

		bw.write("<span>\n");
		if (priority) {
			bw.write("<span d:priority=\"2\">");
		}
		bw.write("<span class=\"t\">");
		bw.write(word.getTrad());
		bw.write("</span>");
		bw.write(" / ");
		bw.write("<span class=\"s\">");
		bw.write(word.getSimp());
		bw.write("</span>");
		bw.write(" (");
		if (priority) {
			bw.write("</span>");
		}
		bw.write("<span class=\"syntax\"><span d:pr=\"\">");
		if (pinyin) {
			bw.write(Pinyin.format(word.getPinyin()));
		}
		else {
			bw.write(Zhuyin.format(word.getPinyin()));
		}
		bw.write("</span></span>");
		if (priority) {
			bw.write("<span d:priority=\"2\">");
		}
		bw.write(")");
		if (priority) {
			bw.write("</span>");
		}
		bw.write("</span>\n");

		// Definition

		bw.write("<ul>\n");

		for (String def : word.getDefs()) {
			bw.write("<li>" + def + "</li>");
		}

		bw.write("</ul>\n");
		bw.write("</d:entry>\n");
	}

	private void _insertIndex(BufferedWriter bw, String value, String title)
		throws IOException {

		bw.write("<d:index d:value=\"");
		bw.write(Word.escape(value));
		bw.write("\" d:title=\"");
		bw.write(Word.escape(title));
		bw.write(" \" />\n");
	}

	private String _readLine() throws Exception {
		for (;;) {
			String line = _br.readLine();

			if (line == null) {
				return null;
			}

			line = line.trim();

			if (!line.startsWith("#") && line.length() > 0) {
				return line;
			}
			else if (line.startsWith("#! date=")) {
				line = line.substring(line.indexOf('=') + 1);

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

				Date date = formatter.parse(line);

				formatter = new SimpleDateFormat("dd MMMM yyyy");

				_cedict_release_date = formatter.format(date);
			}
		}
	}

	private void _writePlist(BufferedWriter bw, boolean pinyin)
		throws IOException {

		String bundleIdentifier = pinyin ? "com.caorongjin.pingguo.PingguoPY" : "com.caorongjin.pingguo.PingguoZY";
		String bundleName = pinyin ? "Pingguo PY" : "Pingguo ZY";
		String edition = pinyin ? "(Pinyin Edition)" : "(Zhuyin Edition)";
		String year = String.valueOf(_calendar.get(Calendar.YEAR));

		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		bw.write(
			"<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" " +
			"\"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n");
		bw.write("<plist version=\"1.0\">\n");
		bw.write("<dict>\n");
		bw.write("\t<key>CFBundleDevelopmentRegion</key>\n");
		bw.write("\t<string>English</string>\n");
		bw.write("\t<key>CFBundleIdentifier</key>\n");
		bw.write("\t<string>" + bundleIdentifier + "</string>\n");
		bw.write("\t<key>CFBundleName</key>\n");
		bw.write("\t<string>" + bundleName + "</string>\n");
		bw.write("\t<key>CFBundleShortVersionString</key>\n");
		bw.write("\t<string>" + _version + "</string>\n");
		bw.write("\t<key>DCSDictionaryCopyright</key>\n");
		bw.write(
			"\t<string>Pingguo Dictionary " + _version + " " + edition +
			", Copyright &amp;#169; 2008-" + year + " Alexander Chow." +
			"&lt;br /&gt;&lt;br/&gt;Dictionary contents are distributed as " +
			"CC-CEDICT by MDBG (http://www.mdbg.net) under the Creative " +
			"Commons Attribution-Share Alike 3.0 license.</string>\n");
		bw.write("\t<key>DCSDictionaryManufacturerName</key>\n");
		bw.write("\t<string>Alexander Chow</string>\n");
		bw.write("\t<key>DCSDictionaryFrontMatterReferenceID</key>\n");
		bw.write("\t<string>front_back_matter</string>\n");
		bw.write("\t<key>DCSDictionaryPrefsHTML</key>\n");
		bw.write("\t<string>Dictionary_prefs.html</string>\n");
		bw.write("\t<key>DCSDictionaryXSL</key>\n");
		bw.write("\t<string>Dictionary.xsl</string>\n");
		bw.write("\t<key>DCSDictionaryDefaultPrefs</key>\n");
		bw.write("\t<dict></dict>\n");
		bw.write("</dict>\n");
		bw.write("</plist>");
	}

	private void _writePostFix(BufferedWriter bw, boolean pinyin)
		throws IOException {

		String edition = pinyin ? "(Pinyin Edition)" : "(Zhuyin Edition)";

		bw.write(
			"<d:entry id=\"front_back_matter\" " +
			"d:title=\"Front/Back Matter\">\n");
		bw.write(
			"<h1><b>Pingguo Chinese-English Dictionary v" + _version + ", " +
			edition + "</b></h1>\n" + "<div>" +
			"Copyright &#169; 2008-" + _calendar.get(Calendar.YEAR) +
			" Alexander Chow. Dictionary contents are distributed as " +
			"<a href=\"http://bit.ly/cc-cedict\">CC-CEDICT</a> (released " +
			_cedict_release_date + ") under the " +
			"<a href=\"http://creativecommons.org/licenses/by-sa/3.0/\">" +
			"Creative Commons Attribution-Share Alike 3.0</a> license." +
			"</div>\n");
		bw.write("</d:entry>\n");
		bw.write("</d:dictionary>");
	}

	private void _writePrefix(BufferedWriter bw) throws IOException {
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		bw.write(
			"<d:dictionary xmlns=\"http://www.w3.org/1999/xhtml\" " +
			"xmlns:d=\"http://www.apple.com/DTDs/DictionaryService-1.0.rng\">\n");
	}

	private static Calendar _calendar = GregorianCalendar.getInstance();
	private static String _cedict_release_date = "??";

	private static String _version;
	private static final String DICTIONARY_PLIST = "Dictionary.plist";
	private static final String DICTIONARY_XML = "Dictionary.xml";

	private BufferedReader _br;
	private long _id = 0;
	private MultiValueMap _mvp;
	private BufferedWriter _pyp;
	private BufferedWriter _pyx;
	private BufferedWriter _zyp;
	private BufferedWriter _zyx;

}