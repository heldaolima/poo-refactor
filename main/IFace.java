package main;

import java.util.ArrayList;
import java.util.Scanner;

import javax.lang.model.util.ElementScanner14;

import templates.RedeSocial;

public class IFace implements RedeSocial {
    // protected Logado logado = null;
    protected PseudoUser pseudoLogado = null;
    private ArrayList<Usuario> usuarios = new ArrayList<Usuario>();
    protected ArrayList<Comunidade> comunidades = new ArrayList<Comunidade>();
    protected FeediFace feed = new FeediFace();
    public Scanner sc = new Scanner(System.in);

    @Override
    public boolean novaConta() {
        String nome, login, senha;
        System.out.println("Caso queira cancelar, insira -1 no lugar do nome ou no lugar de login");
        System.out.print("Insira o seu nome e sobrenome: ");
        nome = sc.nextLine();
        
        while (nome == "" || nome == null) {
            System.out.print("Nome inválido! Insira o seu nome e sobrenome:");
            nome = sc.nextLine();
        }
        if (nome.equals("-1")) return false;

        System.out.print("Crie um login: ");
        login = sc.nextLine();
        
        while (login == "" || login == null || loginUsado(login) || !loginValido(login)) {
            System.out.print("Login inválido! Crie um login: ");
            login = sc.nextLine();
        }

        if (login.equals("-1")) return false;

        System.out.print("Crie uma senha: ");
        senha = sc.nextLine();

        while (senha == "" || senha == null) {
            System.out.println("Senha inválida! Crie uma senha: ");
            senha = sc.nextLine();
        }

        usuarios.add(new Usuario(nome, login, senha));
        System.out.println();
        return true;
    }

    @Override
    public Logado login() {
        System.out.print("Insira o seu login: ");        
        Usuario user = getUsuario(sc.nextLine());

        System.out.print("Insira a sua senha: ");
        if (user != null && user.getSenha().equals(sc.nextLine())) {
            usuarios.remove(user); //temporariamente removido
            pseudoLogado = new PseudoUser(user.getNome(), user.getLogin());
            return (new Logado(user.getNome(), user.getLogin(), user.getSenha(), user.amigos, user.solicitacoes, user.comunidade, user.comunidadesMembro, user.atributos));
        }
        return null;
    }

    @Override
    public boolean novoAtributo(Logado logado) {
        System.out.println("Caso queira cancelar, insira -1 no lugar do nome ou da descrição");

        Atributo novo = new Atributo();

        System.out.print("Nome do atributo: ");
        while (!novo.setNome(sc.nextLine()))
            System.out.print("Nome do atributo: ");

        if (novo.getNome().equals("-1")) 
            return false;

        System.out.print("Descrição do atributo: ");
        while (!novo.setDescricao(sc.nextLine()))
            System.out.print("Descrição do atributo: ");
        
        if (novo.getDescricao().equals("-1")) 
            return false;

        System.out.println(novo.toString());

        logado.novoAtributo(novo);
        return true;

    }

    @Override
    public boolean editarAtributo(Logado logado) {
        if (logado.qtdAtributos() == 0) {
            System.out.println("Não há atributos!");
            return false;
        }
        
        logado.mostrarAtributos();
        int i;
        System.out.print("Insira o número do atributo que quer modificar (-1 cancela): ");
        i = sc.nextInt();
        sc.nextLine();

        i = i-1;
        System.out.println("\nEditando: \n"+logado.getAtributo(i).toString()+"\n");

        if (i < 0 || i >= logado.qtdAtributos()) return false;
        
        int esc = 0;
        while (esc != -1) {
            System.out.println("[1] Editar nome\n[2] Editar descrição\n[-1] Encerrar");
            System.out.print("Escolha: ");
            esc = sc.nextInt();
            sc.nextLine();
            
            String novo;
            if (esc == 1) {
                System.out.print("\nInsira o novo nome: ");
                novo = sc.nextLine();
                while (novo == null || novo == "") {
                    System.out.print("Entrada inválida! Insira o novo nome: ");
                    novo = sc.nextLine();
                }
                if (novo == "-1") return false;

                logado.editarAtributoNome(i, novo);
            }
            else if (esc == 2) {
                System.out.print("\nInsira a nova descrição: ");
                novo = sc.nextLine();
                while (novo == null || novo == "") {
                    System.out.print("Entrada inválida! Insira a nova descrição: ");
                    novo = sc.nextLine();
                }
                if (novo == "-1") return false;

                logado.editarAtributoDescricao(i, novo);
            }
        }
        return true;
    }

    @Override
    public boolean enviarSolicitacao(Logado logado) {
        ArrayList<Integer> disponiveis = mostrarUsuariosSolicitacao(logado);
        
        if (disponiveis.size() == 0) {
            System.out.println("Não há usuários disponíveis");
            return false;
        }

        System.out.print("Mandar solicitação para [índice]: ");
        int i = sc.nextInt();
        sc.nextLine();

        if (i < 0 || i >= usuarios.size() || !disponiveis.contains(i))
            return false;
        
        Solicitacao sol = new Solicitacao(logado.getNome(), logado.getLogin(), logado.qtdAmigos());
        
        usuarios.get(i).recebeSolicitacao(sol);

        return true;        
    }

    @Override
    public boolean responderSolicitacao(Logado logado) {
        if (logado.qtdSolicitacoes() == 0) {
            System.out.println("Não há solicitações");
            return false;
        }
        logado.mostrarSolicitacoes();
        System.out.println("Responder solicitção de [índice]: ");
        int i = sc.nextInt();
        sc.nextLine();
        i--;
        
        if (i < 0 || i >= logado.qtdSolicitacoes()) {
            System.out.println("Entrada inválida");
            return false;
        }

        System.out.println("[1] Aceitar\n[2] Recusar\n[-1] Cancelar");
        System.out.print("Escolha: ");
        int esc = sc.nextInt();
        sc.nextLine(); 
        
        if (esc < 0 || esc > 2) 
            return false;
        
        else if (esc == 2) {
            logado.solicitacoes.remove(logado.getSolicitacao(i));
            System.out.println("Solicitação recusada");
        }
        else 
            logado.respoderSolicitacao(logado.getSolicitacao(i), usuarios);   
        
        return true;
        
    }

    @Override
    public boolean enviarMensagem(Logado logado) {
        if (logado.qtdAmigos() == 0) {
            System.out.println("Você tem 0 amigos");
            return false;
        }
        System.out.println("Você tem "+logado.qtdAmigos()+" amigos");
        logado.mostrarAmigos();
        System.out.println("Enviar mensagem para (índice): ");
        int i = sc.nextInt();
        sc.nextLine();
        i--;
        if (i < 0 || i >= logado.qtdAmigos()) {
            System.out.println("Entrada inválida");
            return false;
        }
       
       logado.getAmigo(i).mostrarMensagens();

        Mensagem msg = new Mensagem();
        msg.setSender(logado.getLogin());
        System.out.println("Insira a nova mensgem: ");
        msg.setContent(sc.nextLine());

        logado.getAmigo(i).novaMensagem(msg); //o amigo tem a mensagem;

        return logado.enviarMensagem(logado.getAmigo(i), msg, usuarios); //para que o Usuario destinatário receba a mensagem
    }

    @Override
    public boolean novaComunidade(Logado logado) {
        if (logado.temComunidade()) {
            System.out.println("Esta conta já criou uma comunidade:");
            System.out.println(logado.comunidadeToString());
            return false;
        }
        System.out.println("Insira -1 no lugar do nome ou da descrição para cancelar");
        System.out.print("Insira o nome da comunidade: ");
        String nome = sc.nextLine();
        while (nome == null || nome == "") {
            System.out.print("Entrada inválida! Insira o nome da comunidade: ");
            nome = sc.nextLine();
        }
        
        if (nome.equals("-1")) 
            return false;
        
        System.out.print("Insira a descrição da comunidade: ");
        String descricao = sc.nextLine();
        while (descricao == null || descricao == "") {
            System.out.print("Entrada inválida! Insira a descrição da comunidade: ");
            descricao = sc.nextLine();
        }

        if (descricao.equals("-1")) return false;

        Comunidade comunidade = new Comunidade(nome, descricao, pseudoLogado);
        comunidades.add(comunidade);

        return logado.criarComunidade(comunidade);
    }

    @Override
    public boolean virarMembroComunidade(Logado logado) {
        ArrayList<Integer> disponiveis = mostrarComunidades(logado);
        if (disponiveis.size() == 0) {
            System.out.println("Não há comunidades disponíveis");
            return false;
        }

        System.out.println("Entrar na comunidade (indice): ");
        int esc = sc.nextInt();
        sc.nextLine();
        
        if (!disponiveis.contains(esc)) {
            System.out.println("Entrada inválida");
            return false;
        }
        
        comunidades.get(esc).addMembro(pseudoLogado);
        logado.virarMembro(comunidades.get(esc));

        return true;
    }

    @Override
    public void mostrarFeed(Logado logado) {
        logado.verFeed(feed.publicacoes);
    }

    @Override
    public boolean publicarNoFeed(Logado logado) {
        String conteudo;
        System.out.println("No que você está pensando? (-1) cancela");
        
        conteudo = sc.nextLine();
        while (conteudo == null || conteudo == "") {
            System.out.println("No que você está pensando? (-1) cancela");
            conteudo = sc.nextLine();
        }
        
        if (conteudo.equals("-1")) 
            return false;
        

        System.out.println("Defina a privacidade da publicação:\n[1] Pública\n[2] Privada\n[-1] Cancelar publicação");
        System.out.print("Sua escolha: ");
        int esc = sc.nextInt();
        sc.nextLine();

        while (esc != -1 && (esc < 1 || esc > 2)) {
            System.out.print("Entrada inválida! Insira a sua escolha de privacidade: ");
            esc = sc.nextInt();
            sc.nextLine();
        }

        if (esc == -1) 
            return false;
        else if (esc == 1) 
            feed.publicar(new Publicacao(pseudoLogado, conteudo, false));
        else
            feed.publicar(new Publicacao(pseudoLogado, conteudo, true));

        return true;
    }

    @Override
    public void resumoDaConta(Logado logado) {
        logado.resumoDaConta();
    }

    @Override
    public Logado logOut(Logado logado) {
        usuarios.add((Usuario) logado); 
        pseudoLogado = null;
        return null;
    }
    
    public Usuario getUsuario(String login) {
        for (Usuario user: usuarios) {
            if (user.getLogin().equals(login))
                return user;
        }
        return null;
    }

    public boolean loginUsado(String login) {
        for (Usuario user: usuarios) {
            if (user.getLogin().equals(login)) 
                return true;
        }
        return false;
    }

    public boolean loginValido(String login) {
        if (login.contains(" ") || login.contains("@"))
            return false;
        return true;
    }

    public ArrayList<Integer> mostrarUsuariosSolicitacao(Logado logado) {
        ArrayList<Integer> ans = new ArrayList<Integer>();
        int i = 0;
        for (Usuario user: usuarios) {
            if (logado.verificarAmizade(user.getLogin()) == -1 && user.verificarSolicitacao(logado.getLogin()) == -1 && logado.verificarSolicitacao(user.getLogin()) == -1) {
                System.out.println("["+i+"] " + user.dadosBasicos());
                ans.add(i);
            }
            i++;
        }
        return ans;
    }

    public int qtdComunidades() {
        return comunidades.size();
    }

    public ArrayList<Integer> mostrarComunidades(Logado logado) {
        ArrayList<Integer> disponiveis = new ArrayList<Integer>();
        int i = 0;
        for (Comunidade com: comunidades) {
            if (!logado.eDono(com) && !logado.eMembro(com)) {
                System.out.println("["+i+"] "+com.toString());
                disponiveis.add(i);
            }
        }
        return disponiveis;
    }

    public void mostrarUsuarios() {
        for (Usuario u: usuarios) {
            System.out.println(u.dadosBasicos());
        }
    }
}
