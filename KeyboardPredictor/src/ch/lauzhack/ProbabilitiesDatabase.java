package ch.lauzhack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;

import org.json.JSONObject;

public class ProbabilitiesDatabase {

	private final Map<String, Long> ngramCounts = new TreeMap<>();
	private final Map<Integer, Long> totalCounts = new TreeMap<>();
	private final int maxN;
	private final String alphabet = "abcdefghijklmnopqrstuvwxyz -'";

	public ProbabilitiesDatabase(int maxN) {
		this.maxN = maxN;
	}

	public void loadTextFile(String filename) {
		System.out.println("Begin to load " + filename);
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(filename));
			final Queue<Character> queue = new LinkedList<>();
			int read = bufferedReader.read();
			while (read != -1) {
				final char c = Character.toLowerCase((char) read);
				if (alphabet.contains("" + c)) {
					queue.add(c);
					if (queue.size() > maxN) {
						queue.remove();
					}
					final String ngram = iterableToString(queue);
					final int n = ngram.length();
					for (int i = 0; i < n; i++) {
						addNgram(ngram.substring(i, n));
					}
				} else {
					queue.clear();
				}

				read = bufferedReader.read();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Finish to load " + filename);
	}

	private void addNgram(String ngram) {
		final Long ngramCount = ngramCounts.get(ngram);
		if (ngramCount == null) {
			ngramCounts.put(ngram, 1l);
		} else {
			ngramCounts.put(ngram, ngramCount + 1);
		}
		final int n = ngram.length();
		final Long totalCount = totalCounts.get(n);
		if (totalCount == null) {
			totalCounts.put(n, 1l);
		} else {
			totalCounts.put(n, totalCount + 1);
		}
	}

	private <T> String iterableToString(Iterable<T> it) {
		final StringBuilder stringBuilder = new StringBuilder();
		for (T element : it) {
			stringBuilder.append(element);
		}
		return stringBuilder.toString();
	}

	public double getNgramProbabilities(String ngram) {
		final int n = ngram.length();
		if (n > maxN) {
			throw new IllegalArgumentException("the ngram must not be longer than " + maxN);
		}
		if (ngramCounts.isEmpty() || totalCounts.isEmpty()) {
			throw new IllegalStateException("Not initialized");
		}
		final Long ngramCount = ngramCounts.get(ngram);
		if (ngramCount == null) {
			return 0;
		}
		final Long totalCount = totalCounts.get(n);
		if (totalCount == null) {
			throw new IllegalStateException("Unknown error");
		}
		return (double) ngramCount / (double) totalCount;
	}

	public void createSave(String filename) {
		BufferedWriter bufferedWriter = null;
		try {
			final File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			final JSONObject jsonObject = new JSONObject();
			jsonObject.put("maxN", maxN);
			final JSONObject ngramCountsJsonObject = new JSONObject();
			for (Entry<String, Long> entry : ngramCounts.entrySet()) {
				ngramCountsJsonObject.put(entry.getKey(), entry.getValue());
			}
			jsonObject.put("ngramCounts", ngramCountsJsonObject);
			final JSONObject totalCountsJsonObject = new JSONObject();
			for (Entry<Integer, Long> entry : totalCounts.entrySet()) {
				totalCountsJsonObject.put("" + entry.getKey(), entry.getValue());
			}
			jsonObject.put("totalCounts", totalCountsJsonObject);
			bufferedWriter.write(jsonObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadSave(String filename) {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(filename));
			String read = bufferedReader.readLine();
			final JSONObject jsonObject = new JSONObject(read);
			final int loadedMaxN = jsonObject.getInt("maxN");
			if (loadedMaxN < maxN) {
				throw new IllegalArgumentException();
			}
			final JSONObject ngramCountsJsonObject = jsonObject.getJSONObject("ngramCounts");
			for (Object key : ngramCountsJsonObject.keySet()) {
				final String ngram = (String) key;
				final long count = ngramCountsJsonObject.getLong(ngram);
				ngramCounts.put(ngram, count);
			}
			final JSONObject totalCountsJsonObject = jsonObject.getJSONObject("totalCounts");
			for (Object key : totalCountsJsonObject.keySet()) {
				final String n = (String) key;
				final long count = totalCountsJsonObject.getLong(n);
				totalCounts.put(Integer.parseInt(n), count);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadOrCreate(String textFilename, String saveFilename) {
		final File saveFile = new File(saveFilename);
		if (saveFile.exists()) {
			loadSave(saveFilename);
		} else {
			loadTextFile(textFilename);
			createSave(saveFilename);
		}
	}

}
