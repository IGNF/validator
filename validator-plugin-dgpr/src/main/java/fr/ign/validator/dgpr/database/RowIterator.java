package fr.ign.validator.dgpr.database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class RowIterator implements Iterator<String[]>, Closeable {

	private ResultSet rs ;

	private String[] current ;

	public RowIterator(ResultSet rs) throws SQLException {
		this.rs = rs;
		readOne();
	}


	@Override
	public boolean hasNext() {
		return current != null;
	}


	@Override
	public String[] next() {
		String[] result = current;
		try {
			readOne();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return result;
	}


	@Override
	public void close() throws IOException {
		try {
			rs.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}


	public String[] getHeader() throws SQLException {
		int size = rs.getMetaData().getColumnCount();
		String[] result = new String[size];
		for (int i = 0; i < size; i++) {
			result[i] = rs.getMetaData().getColumnName(i + 1);
		}
		return result;
	}


	public int getColumn(String columnName) throws SQLException {
		for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
			String name = rs.getMetaData().getColumnName(i + 1);
			if (name.toLowerCase().equals(columnName.toLowerCase())) {
				return i;
			}
		}
		return -1;
	}

	private void readOne() throws SQLException {
		if ( ! rs.next() ) {
			current = null;
			return;
		}
		current = new String[rs.getMetaData().getColumnCount()];
		for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
			current[i] = rs.getString(i + 1);
		}
	}
}
