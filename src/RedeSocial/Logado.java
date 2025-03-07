package src.RedeSocial;
import java.util.ArrayList;

import src.usuario.Amigo;
import src.usuario.Atributo;
import src.usuario.Mensagem;
import src.usuario.Solicitacao;

public class Logado extends Usuario {
    
    public Logado(String nome, String login, String senha, ArrayList<Amigo> amigos, ArrayList<Solicitacao> solicitacoes, Comunidade comunidade,  ArrayList<String> comunidadesMembro, ArrayList<Atributo> atributos) {
        super(nome, login, senha);
        this.amigos = amigos;
        this.solicitacoes = solicitacoes;
        this.comunidade = comunidade;
        this.comunidadesMembro = comunidadesMembro;
        this.atributos = atributos;
    }
    
    public Logado(Logado logado) {
        super(logado.getNome(), logado.getSenha(), logado.getSenha());
        this.amigos = logado.amigos;
        this.solicitacoes = logado.solicitacoes;
        this.comunidade = logado.comunidade;
        this.comunidadesMembro = logado.comunidadesMembro;
        this.atributos = logado.atributos;
    }
    
    public void editarAtributoNome(int i, String nome) {
        atributos.get(i).setNome(nome);
    }

    public void editarAtributoDescricao(int i, String descricao) {
        atributos.get(i).setDescricao(descricao);
    }
    
    public void respoderSolicitacao(Solicitacao sol, ArrayList<Usuario> usuarios) {
        Amigo amigo = new Amigo(sol.getNome(), sol.getLogin());
        this.adicionarAmigo(amigo);

        for (Usuario user: usuarios) {
            if (user.getLogin().equals(sol.getLogin()))
                user.adicionarAmigo(new Amigo(this.getNome(), this.getLogin()));
        }
    }

    public boolean enviarMensagem(Amigo dest, Mensagem msg, ArrayList<Usuario> usuarios) {
        for(Usuario user: usuarios) { // a instancia user de amigo
            if (user.getLogin().equals(dest.getLogin())) {
                user.receberMensagem(this.getLogin(), msg);
                return true;
            } 
        }
        return false;
    }

    public void verFeed(ArrayList<Publicacao> posts) {
        int cont = 0;
        for (Publicacao post: posts) {
            if (!post.getPrivado() || post.getAutor().getLogin().equals(this.getLogin())){
                System.out.println(post.toString());
                cont++;
            }
            else if (this.verificarAmizade(post.getAutor().getLogin()) != -1) {
                System.out.println(post.toString());
                cont++;
            }
        }
        if (cont == 0)
            System.out.println("Não há publicações...");
    }

}
