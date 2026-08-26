package es.alexrotaru.app_profesors.dto;

public class LoginRequest {
	private String correo;
	private String password;
	
	public LoginRequest() {
		
	}
	
	public LoginRequest(String correo, String password) {
		this.correo = correo;
		this.password = password;
	}
	
	public String getCorreo() {
		return this.correo;
	}
	
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
