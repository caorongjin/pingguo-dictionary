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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collection;

import org.apache.commons.collections.map.MultiValueMap;

public class Pingguo {

	public static void main(String[] args) throws Exception {
		BufferedReader br = null;
		BufferedWriter pyw = null;
		BufferedWriter zyw = null;

		try {
			br = new BufferedReader(
				new InputStreamReader(new FileInputStream(args[0]), "UTF8"));
			pyw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(args[1]), "UTF8"));
			zyw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(args[2]), "UTF8"));

			Pingguo pg = new Pingguo(br, pyw, zyw);

			pg.generateDictionary();
		}
		finally {
			if (br != null) {
				br.close();
			}

			if (pyw != null) {
				pyw.close();
			}

			if (zyw != null) {
				zyw.close();
			}
		}
	}

	public Pingguo(BufferedReader br, BufferedWriter pyw, BufferedWriter zyw) {
		_br = br;
		_pyw = pyw;
		_zyw = zyw;
		_mvp = new MultiValueMap();
	}

	public void generateDictionary() throws Exception {
		_writePrefix(_pyw);
		_writePrefix(_zyw);

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
			
			_insertEntry(_pyw, curr, true);
			_insertEntry(_zyw, curr, false);

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

		_buildEnglishIndex(_pyw, true);
		_buildEnglishIndex(_zyw, false);

		_writePostFix(_pyw, true);
		_writePostFix(_zyw, false);
	}

	private String _readLine() throws IOException {
		for (;;) {
			String line = _br.readLine();
		
			if (line == null) {
				return null;
			}
		
			line = line.trim();
		
			if (!line.startsWith("#") && line.length() > 0) {
				return line;
			}
		}
	}

	private void _writePrefix(BufferedWriter bw) throws IOException {
		bw.write(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<d:dictionary xmlns=\"http://www.w3.org/1999/xhtml\" " +
			"xmlns:d=\"http://www.apple.com/DTDs/DictionaryService-1.0.rng\">\n");
	}

	private void _writePostFix(BufferedWriter bw, boolean pinyin)
		throws IOException {
		
		String edition = pinyin ? "(Pinyin Edition)" : "(Zhuyin Edition)";
		
		bw.write(
			"<d:entry id=\"front_back_matter\" d:title=\"Front/Back Matter\">\n" +
			"<h1><b>Pingguo Chinese-English Dictionary v" + _VERSION + ", " + 
			edition + "</b></h1>\n" + "<div>" +
			"Copyright &#169; 2008-2010 Alexander Chow. Dictionary contents are distributed as <a href=\"http://www.mdbg.net/chindict/chindict.php?page=cedict\">CC-CEDICT</a> (released " + _CEDICT_RELEASE_DATE + ") " +
			"under the <a href=\"http://creativecommons.org/licenses/by-sa/3.0/\">Creative Commons Attribution-Share Alike 3.0</a> license." +
			"</div>\n" +
			"</d:entry>\n" +
			"</d:dictionary>");
	}

	private void _buildEnglishIndex(BufferedWriter bw, boolean pinyin) 
		throws Exception {

		Collection<String> defs = _mvp.keySet();
		long index = _id + 1;
		
		for (String def : defs) {
			bw.write("<d:entry id=\"" + (++index) + "\" d:title=\"" + Word.escape(def) + "\">\n");
			_insertIndex(bw, def, def);
			bw.write("<div><h1>" + def + "</h1></div>\n");
			bw.write("<div><ul>\n");

			Collection<Object[]> titleIdPairs = _mvp.getCollection(def);

			for (Object[] titleIdPair : titleIdPairs) {
				String title = (String)titleIdPair[pinyin ? 0 : 1];
				long id = (Long)titleIdPair[2];

				bw.write("<li><a href=\"x-dictionary:r:" + id + "\">" + title + "</a></li>");
			}

			bw.write("</ul></div>\n");
			bw.write("</d:entry>\n");
		}
	}

	private void _insertEntry(BufferedWriter bw, Word word, boolean pinyin) 
		throws Exception {
		
		String title = word.getTitle(pinyin);

		bw.write("<d:entry id=\"" + _id + "\" d:title=\"" + Word.escape(title) + "\">\n");

		// Index

		_insertIndex(bw, word.getTrad(), title);

		if (!word.getTrad().equals(word.getSimp())) {
			_insertIndex(bw, word.getSimp(), title);
		}

		if (pinyin) {
			_insertIndex(bw, word.getPlainPinyin(), title);
			_insertIndex(bw, word.getPinyin(), title);
		}

		// Header must basically follow this format in order for the pronunciation to work
		// <div><h1>make</h1></div><span class="syntax"><span d:pr="">| māk |</span></span>

		// Characters
		
		bw.write("<div><h1>");
		bw.write("<span class=\"t\">");
		bw.write(word.getTrad());
		bw.write("</span>");
		bw.write(" / ");
		bw.write("<span class=\"s\">");
		bw.write(word.getSimp());
		bw.write("</span>");
		bw.write("</h1></div>\n");

		// Phonetic

		bw.write("<span class=\"syntax\"><span d:pr=\"\">| ");
		bw.write(pinyin ? Pinyin.format(word.getPinyin()) : Zhuyin.format(word.getPinyin()));
		bw.write(" |</span></span>");

		// Definition

		bw.write("<div><ul>\n");

		for (String def : word.getDefs()) {
			bw.write("<li>" + def + "</li>");
		}

		bw.write("</ul></div>\n");
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

	private BufferedReader _br;
	private BufferedWriter _pyw;
	private BufferedWriter _zyw;
	private MultiValueMap _mvp;
	private long _id = 0;

	private static final String _CEDICT_RELEASE_DATE = "2010-05-02 21:59:33 GMT ";

	private static final String _VERSION = "1.0.0.2";

}