package fr.ign.validator.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ign.validator.model.Projection;


public class ProjectionRepository {

	private static ProjectionRepository instance = new ProjectionRepository();
	
	private List<Projection> projections = new ArrayList<>();
	
	private ProjectionRepository() {
		try {
			this.loadFromResources();			
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static ProjectionRepository getInstance(){
		return ProjectionRepository.instance;
	}
	
	/**
	 * Find all projections
	 * @return
	 */
	public List<Projection> findAll(){
		return this.projections;
	}
	
	/**
	 * Find projection by code
	 * @param code
	 * @return
	 */
	public Projection findByCode(String code) {
		for (Projection projection : projections) {
			if ( projection.getCode().equals(code) ){
				return projection;
			}
		}
		return null;
	}
	
	/**
	 * Find projection by code
	 * @param code
	 * @return
	 */
	public Projection findByUri(String uri) {
		for (Projection projection : projections) {
			if ( projection.getUri().equals(uri) ){
				return projection;
			}
		}
		return null;
	}

	/**
	 * Load src/main/resources/projection.json
	 * @throws IOException
	 */
	private void loadFromResources() throws IOException{
		InputStream is = getClass().getResourceAsStream("/projection.json");
		ObjectMapper mapper = new ObjectMapper();
		this.projections = mapper.readValue(is, new TypeReference<List<Projection>>(){});
	}



}
