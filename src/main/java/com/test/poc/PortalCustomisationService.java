package com.test.poc;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PortalCustomisationService {

	public static String addImage(MultipartFile multipartFile) {
		return storeImage(multipartFile);
	}


	private static String storeImage(MultipartFile multipartFile) {

		String INSERT_QUERY = "INSERT "
				+ "INTO API_PORTAL_IMG2_DATA(IMG_ID,ID_GROUP,IMG_DATA,DATA_TYPE,IF_NONE_MATCH,CREATED_BY,CREATED_AT,LAST_MODIFIED_BY,LAST_MODIFIED_AT)"
				+ "VALUES (1,'wavemaker.com',?,'image','egjwhgje','shiva',NOW(),'shiva sai kumar',NOW())";

		String SELECT_QUERY = "SELECT " + "COUNT(*) FROM API_PORTAL_IMG2_DATA";

		String UPDATE_QUERY = "UPDATE "
				+ "API_PORTAL_IMG2_DATA SET IMG_DATA = ?,LAST_MODIFIED_BY='shivakumar',LAST_MODIFIED_AT=NOW() where IMG_ID=1";

		String url = "jdbc:postgresql://localhost:5432/gateway";
		String user = "postgres";
		String password = "pramati";
		PreparedStatement statement1 = null;
		PreparedStatement statement2 = null;
		Connection conn = null;

		try {
			DriverManager.registerDriver(new org.postgresql.Driver());
			conn = DriverManager.getConnection(url, user, password);

			statement1 = conn.prepareStatement(SELECT_QUERY);

			ResultSet rs = statement1.executeQuery();

			if (rs.next() && rs.getInt("count") >= 1) {
				statement2 = conn.prepareStatement(UPDATE_QUERY);
			} else {
				statement2 = conn.prepareStatement(INSERT_QUERY);
			}

			byte[] byteArr = null;
			try {
				byteArr = multipartFile.getBytes();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
			//	statement2.setCharacterStream(1, new CharArrayReader(new String(multipartFile.getBytes()).toCharArray()), byteArr.length);
				statement2.setBinaryStream(1, new ByteArrayInputStream(multipartFile.getBytes()),
						multipartFile.getBytes().length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			statement2.executeUpdate();

			return " File Uploaded successfully ";

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement2 != null)
					statement2.close();
				if (statement1 != null)
					statement1.close();
				if (conn != null)
					conn.close();
			} catch (Exception ex) {

			}
		}
		return "Problem while storing an image";
	}

	public static ResponseEntity<byte[]> readImage(HttpServletRequest request) throws IOException {

		final HttpHeaders headers = new HttpHeaders();

		headers.setContentType(new MediaType("text", "css"));

		String SELECT_QUERY = "SELECT " + "img_data,IF_NONE_MATCH from API_PORTAL_IMG2_DATA where img_id = '1'";
		String url = "jdbc:postgresql://localhost:5432/gateway";
		String user = "postgres";
		String password = "pramati";
		InputStream inputStream = null;
		PreparedStatement statement = null;
		Connection conn = null;
		ResultSet rs = null;
		String etag = null;
		try {

			DriverManager.registerDriver(new org.postgresql.Driver());

			conn = DriverManager.getConnection(url, user, password);

			statement = conn.prepareStatement(SELECT_QUERY);

			rs = statement.executeQuery();

			if (rs == null) {
				return null;
			}
			if (rs.next()) {
				inputStream = rs.getBinaryStream("img_data");
				etag = rs.getString("IF_NONE_MATCH");
			}
			return new ResponseEntity<byte[]>(IOUtils.toByteArray(inputStream), headers, HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (inputStream != null)
					inputStream.close();
				if (statement != null)
					statement.close();
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
			}
		}
		return null;
	}

}
