/*Diovane Soligo - 02/2017 - Servidor de recebimento de dados da tomada inteligente */
package recebedadosphantom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecebeDadosPhantom {
    
/******************************************************************************/    
    static class ArmazenaDadosPhanton implements Runnable{
        
        Socket cliente;

        public ArmazenaDadosPhanton(Socket cliente) {
            this.cliente = cliente;
        }
       
        @Override
        public void run() {
            try {
                String ClienteIP = cliente.getInetAddress().getHostAddress().toString();//pega o IP do cliente (tomada)
                
                byte[] msg = new byte[1024];//cria buffer
                int tamanho = cliente.getInputStream().read(msg);//armazena o que vem do cliente no buffer msg, e o tamanho na variavel tamanho
                
                String msgString = new String (msg,"UTF-8");
                
                //System.out.println("\nTAMANHO: "+tamanho);//mostra o tamanho da mensagem vinda do cliente
                //System.out.println("\n\nmsgString: "+msgString);//mostra toda mensagem GET vinda do cliente
                //System.out.println("Cliente IP: "+ClienteIP);
                
                if(tamanho>0){
                        String[] msgLink = new String[tamanho]; //cria um array com o tamanho da mensagem do cliente
                        msgLink = msgString.split(" "); //quebra onde tiver espaço
                        msgString = msgLink[1]; //armazena /?ID=01&VOLTS=....
                        //http://localhost:8080/?ID=1@1&VOLTS=219&CORRENTE=0,1123

                        char comp = '?';
                        char comp2= msgString.charAt(1);

                        if(comp == comp2){//se tiver o caracter '?' executa
                               System.out.println("..ARMAZENAR DADOS NO SGBD..");
                               System.out.println("Cliente IP: "+ClienteIP);
                               System.out.println("Cliente Dados: "+msgLink[1]);
                               
                               String[] dados = new String[3];
                               dados = msgString.split("&");
                               //System.out.println("dados: "+dados[0]);
                               //System.out.println("dados: "+dados[1]);
                               //System.out.println("dados: "+dados[2]);
                               
                               String[] aux = new String[2];
                               String ID,V,A;
                               
                               aux = dados[0].split("=");
                               ID = aux[1];
                               
                               aux = dados[1].split("=");
                               V = aux[1];
                               
                               aux = dados[2].split("=");
                               A = aux[1];
                               
                               System.out.println("Tomada ID: "+ID+"\nVolts: "+V+" volts\nCorrente: "+A+" amperes\n");
                               
                               
                               
                        }else{
                               System.out.println("\n\n");
                        }
                 }  
                
                cliente.getOutputStream().write("<html><head><title>Phantom</title></head><body></body></html>".getBytes("ISO-8859-1"));//retorna algo ao cliente
                
                cliente.close(); //encerra conexão
                
            } catch (IOException ex) {
                Logger.getLogger(RecebeDadosPhantom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    }
/******************************************************************************/    
    
/******************************************************************************/    
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket servidor = new ServerSocket(8080);
        
        while(true){
            Socket cliente = servidor.accept();
            ArmazenaDadosPhanton Phanton = new ArmazenaDadosPhanton(cliente);
            new Thread(Phanton).start();
        }
    }
/******************************************************************************/    
}
