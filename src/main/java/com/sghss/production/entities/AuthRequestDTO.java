package com.sghss.production.entities;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String email;
    private String senha;
	public Object getEmail() {
		// TODO Auto-generated method stub
		return null;
	}
	public Object getSenha() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
}
