package com.luizsoftware.buscaEmGrafos;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Grafo_Matriz_Adjacencias {
    int[][] matrizAdjacencias;
    String[] vertices;
    int indiceVertices;
    List nosVisitados= new ArrayList<Integer>();
    //Instancia a matriz conforme a quantidade de vertices.
    public Grafo_Matriz_Adjacencias(int qtdVertices) {
        this.matrizAdjacencias = new int[qtdVertices][qtdVertices];
        this.vertices=new String[qtdVertices];
        indiceVertices=0;
    }
    //Método para criar um novo vertice
    public void addVertice(String nome){
        if(indiceVertices < vertices.length){
            vertices[indiceVertices]=nome;
            indiceVertices++;
        }
    }
    //Método para remover o último vertice
    public void removeVertice(){
        if(indiceVertices>0){
            vertices[indiceVertices-1]="";
            indiceVertices--;
        }
    }
//Método para adicionar as relações (bidirecionais) entre dois vertices d grafo.
    public void addArestaBidirecionais(int iVerticeA, int iVerticeB, int valor){
        matrizAdjacencias[iVerticeA][iVerticeB]=valor;
        matrizAdjacencias[iVerticeB][iVerticeA]=valor;
    }
    //Método para adicionar a relação unidirecional entre o Vertice A para o vertice B
    public void addArestaUnidirecionais(int iVerticeA, int iVerticeB, int valor){
        matrizAdjacencias[iVerticeA][iVerticeB]=valor;
    }
    //Retorna as relações do vertice indicado pelo indice iVertice
    public String relacoesVertices(int iVertice){
        String relacoes="";
        for(int i=0;i< vertices.length;i++){
            relacoes += "\n Vertice: " +i +"=>";
            for(int j=0; j<vertices.length; j++){
                if(matrizAdjacencias[i][j]!=0){
                    relacoes = relacoes +" Vertice " +j
                            +"("+ matrizAdjacencias[i][j] +")";
                }
            }
        }
        return relacoes;
    }
    /*=== Implementar o método buscaProfundidade(iVerticeInicial, iVerticeFinal)
    === que irá buscar em profundidade até encontrar o Vertice Final
    === retornando uma String com o caminho percorrido
    Exemplo: Vertice Inicial 0 => 2 => 3 => 1 ==FIM==
    */

    //Para criar este método usei de base a explicação do pdf que implementava um método recursivo para busca em profundidade
    public String buscaProfundidade(int iVerticeInicial, int iVerticeFinal) {
        return buscaProfundidadeRec(iVerticeInicial, iVerticeFinal, "");
    }

    public String buscaProfundidadeRec(int iVerticeInicial, int iVerticeFinal, String caminho) {
        //valido para ver se o nó passado ja não foi visitado anteriormente
        if (nosVisitados.contains(iVerticeInicial)) {
            return null;
        }
        //adiciono o nó atual no caminho percorrido
        caminho += iVerticeInicial + " => ";
        nosVisitados.add(iVerticeInicial);

        //condicional de parada para a recursão caso tenha encontrado o verticefinal
        if (nosVisitados.contains(iVerticeFinal)) {
            return caminho + " FIM!!";
        }
        //for que percorre a matriz de adjacencias procurando casas que tenha um valor diferente de 0 que indicaria ter alguem na casa
        //caso encontre faz uma chamada para a recursão procurando quem tem ligações com o vertice
        for (int i = 0; i < vertices.length; i++) {
            if (matrizAdjacencias[iVerticeInicial][i] != 0) {
                String resultado = buscaProfundidadeRec(i, iVerticeFinal, caminho);
                if (resultado != null) {
                    return resultado;
                }
            }
        }
        return null;
    }

    public String buscaLargura(int iVerticeInicial,int iVerticeFinal){
        List fila=new ArrayList<>();
        int x =0;

        String caminho="";
        nosVisitados.add(iVerticeInicial);

        do{
            if(matrizAdjacencias[iVerticeInicial][x]!=0){
                caminho+= iVerticeInicial + " => ";
                if(iVerticeInicial==iVerticeFinal){
                    return caminho;
                }
                else {
                    fila.add(x);
                    nosVisitados.add(x);}
            }
            if(x>=vertices.length-1){
                fila.remove(0);
                iVerticeInicial=(Integer) fila.get(0);
                x=0;
            }else {
                x += 1;
            }
        }while(!fila.isEmpty());
        return caminho;
    }

    //me baseie em uma busca heuristica de forma a avaliar as opçoes disponiveis e selecionando a que fosse melhor no momento para chegar no resultado
    //caso aquela rota nao chegasse aonda eu queria retorna e continua a busca em nos nao visitados
    public String buscaGulosa(int iVerticeInicial,int iVerticeFinal){
        boolean[] visitados = new boolean[vertices.length];
        List<Integer> caminho = new ArrayList<>();
        caminho.add(iVerticeInicial);

        int verticeAtual = iVerticeInicial;
        // Enquanto houver vertices no caminho ele continua, caso esteja vazio significa que nao encontrou a rota
        while (!caminho.isEmpty()) {
            // Marca o vértice atual como visitado
            visitados[verticeAtual] = true;

            // Se o vértice atual for o destino, retorna o caminho completo
            if (verticeAtual == iVerticeFinal) {
                String resultado = "";
                for (int i = 0; i < caminho.size(); i++) {
                    resultado += caminho.get(i);
                    if (i < caminho.size() - 1) {
                        resultado += " => ";
                    }
                }
                return resultado + " => FIM!!";
            }
            // Inicializa o próximo vértice como inválido para caso nao encontre ele irá parar o código
            int proximoVertice = -1;
            // Coloquei um número alto para que facilite na hora da validaçao abaixo para pegar o menor peso, logo ele sera substituido
            int menorPeso = 9999;

            // Procura o vizinho não visitado com a menor aresta conectada
            for (int i = 0; i < vertices.length; i++) {
                if (!visitados[i] && matrizAdjacencias[verticeAtual][i] > 0) {
                    if (matrizAdjacencias[verticeAtual][i] < menorPeso) {
                        menorPeso = matrizAdjacencias[verticeAtual][i];
                        proximoVertice = i;
                    }
                }
            }

            if (proximoVertice != -1) {
                //Se encontrou um próximo vértice válido, adiciona ao caminho e atualiza o vertice atual
                caminho.add(proximoVertice);
                verticeAtual = proximoVertice;
            } else {
                // Se não encontrou um próximo vértice, retorna um na fila para continuar ate achar o caminho final com menor custo, e atualiza o vertice atual caso nao esteja vazio
                caminho.remove(caminho.size() - 1);
                if (!caminho.isEmpty()) {
                    verticeAtual = caminho.get(caminho.size() - 1);
                }
            }
        }

        // Se saiu do loop sem encontrar o destino, não há caminho possível
        return "Não foi possível encontrar o caminho.";
    }

    public static void main(String[] args) {
        Grafo_Matriz_Adjacencias meuGrafo=new Grafo_Matriz_Adjacencias(5);
        meuGrafo.addArestaBidirecionais(0, 2, 10);
        meuGrafo.addArestaBidirecionais(0, 3, 5);
        meuGrafo.addArestaBidirecionais(0, 1, 4);
        meuGrafo.addArestaUnidirecionais(2, 3, 8);
        meuGrafo.addArestaUnidirecionais(3, 1, 7);
        meuGrafo.addArestaBidirecionais(4,1,6);
        System.out.println(meuGrafo.relacoesVertices(0));
        System.out.println("======================");
        System.out.println(meuGrafo.buscaProfundidade(3,2));
        System.out.println("======================");
        System.out.println(meuGrafo.buscaLargura(3,2));
        System.out.println("======================");
        System.out.println(meuGrafo.buscaGulosa(0,4));
    }
}
