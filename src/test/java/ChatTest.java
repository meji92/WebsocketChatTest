import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatTest extends TestVerticle {

    AtomicInteger msg = new AtomicInteger(0);
    long start = 0;
    AtomicInteger sentMessages = new AtomicInteger(0);
    String ip = "192.168.1.130";
    boolean akka = false;
    boolean[] recievedMessages;
    int users = 40;
    int messages = 500;
    int time = 5000;
    int extra = 100000;
    @Test
    public void test1(){
        test();
    }
    @Test
    public void test2(){
        test();
    }
    @Test
    public void test3(){
        test();
    }
    @Test
    public void test4(){
        test();
    }
    @Test
    public void test5(){
        test();
    }
    @Test
    public void test6(){
        test();
    }
    @Test
    public void test7(){
        test();
    }
    @Test
    public void test8(){
        test();
    }
    @Test
    public void test01(){
        test();
    }
    @Test
    public void test0(){
        test();
    }
    @Test
    public void test9(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long result = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("results.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                System.out.print(result +" + "+line);
                result += Long.parseLong(line);
                System.out.println(" = "+result);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println (result/10);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("results.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.close();
        PrintWriter writer2 = null;
        try {
            writer2 = new PrintWriter(new FileOutputStream(new File("results2.txt"), true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer2.append(Integer.toString(users)+" "+Long.toString(result/10)+"\n");
        writer2.close();
        VertxAssert.testComplete();
    }



    public void test(){
        recievedMessages = new boolean[messages*users];
        start = 0;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testClients(users, messages, time, extra + 1000);
        //testClientsMax(10, 4000, 10000);
    }

    public void testClients(int users, long messages, long time, int extra) {
        //listenerClientHalfTime("LUser"+Double.toString((Math.random()*100)), time + extra, totalMessages*2);
        listenerClient("/////////////////////////////////////////////User" + Double.toString(Math.random()), time + extra, messages * users);
        for (int i = 0; i<users; i++){
            //senderClient("user" + Double.toString(i + (Math.random() * 100)), (time / (totalMessages / users)), time);
            senderClient("user" + Double.toString(i + Math.random()), time/messages, time);
        }
    }

    public void testClientsMax(int users, long time, int extra) {
        /**int users = 10;
         int totalMessages = 5000;
         int time = 5000;
         int extra = 1000;**/
        listenerClient("user", time + extra, users * time);
        for (int i = 0; i<users; i++){
            senderClient("user"+Double.toString(i+(Math.random()*100)), 1, time);
        }
    }

    public void senderClient(final String name, final long waitTime, final long totalTime) {
        //System.out.println(waitTime+" "+totalTime);
        // Setting host as localhost is not strictly necessary as it's the default
        final String auxip;
        if (akka == true) {
            auxip = getIP();
        }else{
            auxip = ip;
        }
        vertx.setTimer(1000, new Handler<Long>() {
            public void handle(Long arg0) {
                vertx.createHttpClient().setHost(auxip).setPort(9000).connectWebsocket("/chat", new Handler<WebSocket>() {
                    //vertx.createHttpClient().setHost("192.168.43.225").setPort(8080).connectWebsocket("/myapp", new Handler<WebSocket>() {
                    @Override
                    public void handle(final WebSocket websocket) {
                        websocket.dataHandler(new Handler<Buffer>() {
                                                  public void handle(Buffer data) {
                                                      /**JsonNode message = null;
                                                       ObjectMapper mapper = new ObjectMapper();
                                                       try {
                                                       message = mapper.readTree(data.getBytes());  //message to Json
                                                       } catch (IOException e) {
                                                       e.printStackTrace();
                                                       }
                                                       String respuesta = message.get("message").toString();
                                                       System.out.println(data);**/
                                                  }
                                              }

                        );
                        JsonObject json = new JsonObject();
                        json.putString("chat", "chat");
                        json.putString("user", name);
                        if (start == 0) {
                            start = System.currentTimeMillis();
                        }
                        websocket.writeTextFrame(json.toString());
                        // Wait 1 second to be sure that the dataHandler has been created
                        final long timerID = vertx.setPeriodic(waitTime, new Handler<Long>() {
                            public void handle(Long arg0) {
                                JsonObject json2 = new JsonObject();
                                json2.putString("user", name);
                                json2.putString("message", Integer.toString(sentMessages.getAndAdd(1)));
                                websocket.writeTextFrame(json2.toString());
                            }
                        });
                        vertx.setTimer(totalTime, new Handler<Long>() {
                                    public void handle(Long arg0) {
                                        vertx.cancelTimer(timerID);
                                        //System.out.println(name+": "+Integer.toString(sentMessages));
                                    }
                                }

                        );
                    }
                });
            }
        });
    }

            public void listenerClient(final String name, final long totalTime, final long messages) {
                // Setting host as localhost is not strictly necessary as it's the default
                String auxip;
                if (akka == true) {
                    auxip = getIP();
                }else{
                    auxip = ip;
                }
                vertx.createHttpClient().setHost(auxip).setPort(9000).connectWebsocket("/chat", new Handler<WebSocket>() {
                    //vertx.createHttpClient().setHost("192.168.43.225").setPort(8080).connectWebsocket("/myapp", new Handler<WebSocket>() {
                    @Override
                    public void handle(final WebSocket websocket) {
                        websocket.dataHandler(new Handler<Buffer>() {
                                                  public void handle(Buffer data) {
                                                      JsonNode message = null;
                                                      ObjectMapper mapper = new ObjectMapper();
                                                      try {
                                                          message = mapper.readTree(data.getBytes());  //message to Json
                                                      } catch (IOException e) {
                                                          e.printStackTrace();
                                                      }
                                                      String respuesta = message.get("message").asText();
                                                      recievedMessages[Integer.parseInt(respuesta)] = true;
                                                      msg.addAndGet(1);
                                                      if (messages == msg.get()) {
                                                          long time = System.currentTimeMillis() - start;
                                                          PrintWriter writer = null;
                                                          try {
                                                              writer = new PrintWriter(new FileOutputStream(new File("results.txt"), true));
                                                          } catch (FileNotFoundException e) {
                                                              e.printStackTrace();
                                                          }
                                                          writer.append(Long.toString(time) + "\n");
                                                          writer.close();
                                                          System.out.println("Tiempo:  " + time);
                                                          System.out.println(msg.get() + "/" + messages + "/" + Integer.toString(sentMessages.get()));
                                                          VertxAssert.testComplete();
                                                      }

                                                  }
                                              }

                        );
                        JsonObject json = new JsonObject();
                        json.putString("chat", "chat");
                        json.putString("user", name);
                        websocket.writeTextFrame(json.toString());
                        vertx.setTimer(totalTime, new Handler<Long>() {
                                    public void handle(Long arg0) {
                                        long time = System.currentTimeMillis() - start;
                                        PrintWriter writer = null;
                                        try {
                                            writer = new PrintWriter(new FileOutputStream(new File("results.txt"), true));
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        writer.append(Long.toString(time) + "\n");
                                        writer.close();
                                        System.out.println("Tiempo:  " + time);
                                        //VertxAssert.assertEquals(messages, msg);
                                        System.out.println(msg.get() + "/" + messages + "/" + Integer.toString(sentMessages.get()));
                                        Boolean ok = true;
                                        for (int i = 0; i < recievedMessages.length; i++) {
                                            if (recievedMessages[i] == false) {
                                                System.out.println("Falta: " + Integer.toString(i));
                                                ok = false;
                                            } else {
                                                recievedMessages[i] = false;
                                            }
                                        }
                                        VertxAssert.assertTrue(ok);
                                        VertxAssert.testComplete();
                                    }
                                }

                        );
                    }
                });
            }



    // HTTP GET request
    private String getIP(){

        String url = "http://"+ip+":9000/";

        URL obj = null;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) obj.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // optional default is GET
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = 0;
        try {
            responseCode = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("\nSending 'GET' request to URL : " + url);
        //System.out.println("Response Code : " + responseCode);

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuffer response = new StringBuffer();
        for (int i = 0; i <= 52; i++){
            try {
                in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /**while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }**/
        try {
            response.append(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String returnedString = response.substring(response.indexOf("/")+2);
        returnedString = returnedString.substring(0, returnedString.indexOf(":"));

        return returnedString;
    }

    public void listenerClientHalfTime(final String name, final long totalTime, final long messages) {
        // Setting host as localhost is not strictly necessary as it's the default
        vertx.createHttpClient().setHost(ip).setPort(9000).connectWebsocket("/chat", new Handler<WebSocket>() {
            //vertx.createHttpClient().setHost("192.168.43.225").setPort(8080).connectWebsocket("/myapp", new Handler<WebSocket>() {
            @Override
            public void handle(final WebSocket websocket) {
                websocket.dataHandler(new Handler<Buffer>() {
                                          public void handle(Buffer data) {
                                              JsonNode message = null;
                                              ObjectMapper mapper = new ObjectMapper();
                                              try {
                                                  message = mapper.readTree(data.getBytes());  //message to Json
                                              } catch (IOException e) {
                                                  e.printStackTrace();
                                              }
                                              String respuesta = message.get("message").asText();
                                              recievedMessages[Integer.parseInt(respuesta)] = true;
                                              /**msg++;
                                               if (messages == msg) {
                                               System.out.println("Tiempo:  " + (System.currentTimeMillis() - start));
                                               System.out.println(msg + "/" + messages+ "/" + Integer.toString(sentMessages.get()*2));
                                               VertxAssert.testComplete();
                                               }**/

                                          }
                                      }

                );
                JsonObject json = new JsonObject();
                json.putString("chat", "chat");
                json.putString("user", name);
                websocket.writeTextFrame(json.toString());
                start = System.currentTimeMillis();
                vertx.setTimer(totalTime / 2, new Handler<Long>() {
                            public void handle(Long arg0) {
                                //System.out.println("Tiempo:  " + (System.currentTimeMillis() - start));
                                //VertxAssert.assertEquals(messages, msg);
                                //System.out.println(msg + "/" + messages+ "/" + Integer.toString(sentMessages.get()*2));
                                //VertxAssert.testComplete();
                            }
                        }

                );
            }
        });
    }

}



