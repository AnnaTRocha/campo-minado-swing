package model;

import java.util.ArrayList;
import java.util.List;

public class Campo {
	private final int LINHA;
	private final int COLUNA;
	
	private boolean aberto;
	private boolean fechado;
	private boolean minado;
	private boolean marcado;
	
	private List<Campo> vizinhos = new ArrayList<Campo>();
	private List<CampoObserver> observers = new ArrayList<>();
	
	public Campo(int linha, int coluna){
		this.LINHA = linha;
		this.COLUNA = coluna;
	}
	
	public void registrarObserver(CampoObserver observer) {
		observers.add(observer);
	}
	
	private void notificarObservadoes(CampoEvento evento) {
		observers.stream()
				 .forEach(o -> o.eventoOcorreu(this, evento));
	}
	
	public boolean adicionarVizinho(Campo vizinho) {
		boolean linhaDiferente = LINHA != vizinho.LINHA;
		boolean colunaDiferente = COLUNA != vizinho.COLUNA;
		boolean diagonal = linhaDiferente && colunaDiferente;
		
		int deltaLinha = Math.abs(LINHA - vizinho.LINHA);
		int deltaColuna = Math.abs(COLUNA - vizinho.COLUNA);
		int deltaGeral = deltaColuna + deltaLinha;
		
		if ((deltaGeral == 1 && !diagonal)
				||(deltaGeral == 2 && diagonal)) {
			
			vizinhos.add(vizinho);
			return true;
		} else {
			return false;
		}
		
	}
	
	public void alternarMarcacao() {
		if(!aberto) {
			marcado = !marcado;
			
			if(marcado) {
				notificarObservadoes(CampoEvento.MARCAR);
			} else {
				notificarObservadoes(CampoEvento.DESMARCAR);
			}
		}
	}
	
	public boolean abrir() {
		if (!aberto && !marcado) {
			aberto = true;
			
			if(minado) {
				notificarObservadoes(CampoEvento.EXPLODIR);
				return true;
			}
			
			setAberto(true);
			notificarObservadoes(CampoEvento.ABRIR);
			
			if(vizinhancaSegura()) {
				vizinhos.forEach(v -> v.abrir());
			}
			return true;
		} else {
			return false;
		}
	}
	
	public boolean vizinhancaSegura() {
		return vizinhos.stream().noneMatch(v -> v.minado);
	}
	
	public boolean isMarcado() {
		return marcado;
	}

	public boolean isAberto() {
		return aberto;
	}

	public void setAberto(boolean aberto) {
		this.aberto = aberto;
		
		if(aberto) {
			notificarObservadoes(CampoEvento.ABRIR);
		}
	}

	public int getLINHA() {
		return LINHA;
	}

	public int getCOLUNA() {
		return COLUNA;
	}

	public void setMarcado(boolean marcado) {
		this.marcado = marcado;
	}

	public boolean isFechado() {
		return !isAberto();
	}

	public void setFechado(boolean fechado) {
		this.fechado = fechado;
	}
	
	boolean objetivoAlcancado() {
		boolean desvendado = !minado && aberto;
		boolean protegido = minado && marcado;
		return desvendado || protegido;
	}
	
	public int minasNaVizinhanca(){
		return (int) vizinhos.stream().filter(v -> v.minado).count();
	}
	
	void reiniciar() {
		aberto = false;
		minado = false;
		marcado = false;
		notificarObservadoes(CampoEvento.REINICIAR);
	}
	
	public boolean isMinado() {
		return minado;
	}

	public void setMinado(boolean minado) {
		this.minado = minado;
	}
	
	void minar() {
		minado = true;
	}
	
	
}
