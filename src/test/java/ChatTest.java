import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatTest extends TestVerticle {

    AtomicInteger msg = new AtomicInteger(0);
    long start = 0;
    AtomicInteger sentMessages = new AtomicInteger(0);
    String ip = "192.168.1.42";
    boolean[] recievedMessages;

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
    /**@Test
    public void test02(){
        test();
    }@Test
    public void test03(){
        test();
    }@Test
    public void test04(){
        test();
    }@Test
    public void test05(){
        test();
    }@Test
    public void test06(){
        test();
    }@Test
    public void test07(){
        test();
    }@Test
    public void test08(){
        test();
    }@Test
    public void test09(){
        test();
    }@Test
    public void test010(){
        test();
    }@Test
    public void test011(){
        test();
    }**/
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
        VertxAssert.testComplete();
    }



    public void test(){
        int users = 100;
        int totalMessages = 5000;
        int time = 5000;
        int extra = 100000;
        recievedMessages = new boolean[totalMessages];
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testClients(users, totalMessages, time, extra+1000);
        //testClientsMax(10, 4000, 10000);
    }

    public void testClients(int users, long totalMessages, long time, int extra) {
        //listenerClientHalfTime("LUser"+Double.toString((Math.random()*100)), time + extra, totalMessages*2);
        listenerClient("LUser" + Double.toString((Math.random() * 100)), time + extra, totalMessages);
        for (int i = 0; i<users; i++){
            senderClient("user" + Double.toString(i + (Math.random() * 100)), (time / (totalMessages / users)), time);
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
        vertx.setTimer(1000, new Handler<Long>() {
            public void handle(Long arg0) {
                vertx.createHttpClient().setHost(ip).setPort(9000).connectWebsocket("/chat", new Handler<WebSocket>() {
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
                                                      msg.addAndGet(1);
                                                      if (messages == msg.get()) {
                                                          long time = System.currentTimeMillis() - start;
                                                          PrintWriter writer = null;
                                                          try {
                                                              writer = new PrintWriter(new FileOutputStream(new File("results.txt"), true));
                                                          } catch (FileNotFoundException e) {
                                                              e.printStackTrace();
                                                          }
                                                          writer.append(Long.toString(time)+"\n");
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
                        start = System.currentTimeMillis();
                        vertx.setTimer(totalTime, new Handler<Long>() {
                                    public void handle(Long arg0) {
                                        long time = System.currentTimeMillis() - start;
                                        PrintWriter writer = null;
                                        try {
                                            writer = new PrintWriter(new FileOutputStream(new File("results.txt"), true));
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        writer.append(Long.toString(time)+"\n");
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



