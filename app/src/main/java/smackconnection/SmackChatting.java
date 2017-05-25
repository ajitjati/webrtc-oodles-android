package smackconnection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.oodles.apprtcandroidoodles.R;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.util.Date;

/**
 * Created by ankita on 25/5/17.
 */

/**
 * Important Links Android
 * 1) https://github.com/igniterealtime/Smack
 */
public class SmackChatting  extends AppCompatActivity {
    AbstractXMPPConnection conn1;
    private static final String DOMAIN = "nimbuzz.com";
    private static final String HOST = "o.nimbuzz.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smack_activity);
        MyLoginTask task = new MyLoginTask();
        task.execute("");
    }

    private class MyLoginTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                SmackConfiguration.DEBUG = true;
                DomainBareJid serviceName = JidCreate.domainBareFrom(DOMAIN);
//                new ConnectionConfiguration("192.168.2.75",5222);

//                ConnectionConfiguration connConfig = new ConnectionConfiguration("192.168.2.75", 5222, SERVICE);
//                XMPPConnection connection = new XMPPConnection(connConfig);
//
//                connection.connect();
                // ejabberd.oodlesbit.in //dev.oodlesbit.in   //("http://192.168.2.75");
                //DomainBareJid serviceName = JidCreate.domainBareFrom("oodlesgroup.com");          //dev.oodlesbit.in   //("http://192.168.2.75");
                XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder()
                        .setUsernameAndPassword("anki1514", "anki1514")
//                        .setHost("192.168.2.75")
                        //.setHost("::1")
//                        .setHostAddress(InetAddress.getByName("::1"))
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setXmppDomain(serviceName)
                        .setPort(5222).setDebuggerEnabled(true);
                conn1 = new XMPPTCPConnection(config.build());
                conn1.addConnectionListener(connectionListener);
                connectAction();
            } catch (Exception e) {
                Log.e("SMACKEXAMPLE", e.getCause() + "Message..." + e.getMessage()+e.fillInStackTrace());
            }
            return "";
        }


        @Override
        protected void onPostExecute(String result) {
            Log.e("PostResult", "postResult");
          /*  Presence presence = new Presence(Presence.Type.available);
            presence.setStatus("Gone fishing");
            try {
                conn1.sendStanza(presence);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String myMUCName = "InstantRoom";
            String myMUCService = "conference.myServer";
            String myMUCfullName = myMUCName + "@" + myMUCService;
            MultiUserChat muc = manager.getMultiUserChat(myMUCfullName);
            muc.create(userName);
                        ChatManager chatManager = ChatManager.getInstanceFor(conn1);
            EntityBareJid jid = null;
            try {
                jid = JidCreate.entityBareFrom("nits9012@nimbuzz.com");
            } catch (XmppStringprepException e) {
                Log.e("XmppStringprepException", e.getCause() + e.getMessage());
                e.printStackTrace();
            }
            Chat chat = chatManager.chatWith(jid);
            sentChat(chat);
            try {
                chat.send("djhfhfhfh");
            } catch (SmackException.NotConnectedException e) {
                Log.e("SentMessage", "NotConnectedException");
                e.printStackTrace();
            } catch (InterruptedException e) {
                Log.e("SentMessage", "InterruptedException");
                e.printStackTrace();
            }

            chatManager.addIncomingListener(new IncomingChatMessageListener() {
                @Override
                public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                    Log.e("newIncomingMessage", from + "" + message + chat);
                    System.out.format("newIncomingMessage: %s.\n", message);
                }
            });
            chatManager.addOutgoingListener(new OutgoingChatMessageListener() {
                @Override
                public void newOutgoingMessage(EntityBareJid to, Message message, Chat chat) {
                    Log.e("newOutgoingMessage", to + "" + message + chat);
                    System.out.format("newOutgoingMessage: %s.\n", message);

                }
            });*/
        }
    }

    private void sentChat(Chat chat) {
        try {
            for (int i = 0; i < 1; i++) {
                String messageString = String.format("Test message number: %d from %s at %s\n", i, "My Name is ankita", new Date());
                chat.send("Nice");
                System.out.format("Attempt to send message number: %d partner: %s Message text: %s.\n",
                        i, chat.getXmppAddressOfChatPartner(), messageString);
            }
        } catch (Exception e) {
            Log.e("NotSent", e.getCause() + e.getMessage() + "");
        }
    }

    ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {
            if (connection.isConnected()) {
                Log.e("connected", "isConnected");
                try {
                    conn1.login("", "");
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("SMACKEXAMPLE", "conn not done");
            }
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            if (connection.isAuthenticated()) {
//                Roster roster = Roster.getInstanceFor(connection);
//                Presence presence;
//                Collection<RosterEntry> entries = roster.getEntries();
//                Log.e("valuesssss",entries.size()+"");
//                for (RosterEntry entry : entries) {
//                    presence = roster.getPresence(entry.getJid());
//                    Log.e("valuesOfEntryId", entry.getJid() + "Name" + entry.getType().name() + "status" + presence.getStatus() + "" + presence.getPriority() + presence.getMode());
//                    System.out.println(entry.getJid());
//                    System.out.println(presence.getType().name());
//                    System.out.println(presence.getStatus());
//                    System.out.println(entry);
//                }
                Log.e("onConnectionListener", "authenticated");
            }
        }

        @Override
        public void connectionClosed() {
            Log.e("onConnectionListener", "connectionClosed");
        }

        @Override
        public void connectionClosedOnError(Exception e) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    connectAction();
//                }
//            });
            if(e instanceof XMPPException.StreamErrorException){
                Log.e("XMPPException",e.getCause()+e.getMessage()+((XMPPException.StreamErrorException) e).getStreamError());
            }

            Log.e("onConnectionListener", "connectionClosedOnError");
        }

        @Override
        public void reconnectionSuccessful() {
            Log.e("onConnectionListener", "reconnectionSuccessful");
        }

        @Override
        public void reconnectingIn(int seconds) {
            Log.e("onConnectionListener", "reconnectingIn");
        }

        @Override
        public void reconnectionFailed(Exception e) {
            Log.e("onConnectionListener", "reconnectionFailed");
        }
    };

    private void loginAction() {
        try {
            conn1.login();
        } catch (XMPPException e) {
            Log.e("XMPPException", e.getCause() + e.getMessage());
            e.printStackTrace();
        } catch (SmackException e) {
            Log.e("SmackException", e.getCause() + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("IOException", e.getCause() + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.e("InterruptedException", e.getCause() + e.getMessage());
            e.printStackTrace();
        }
    }

    private void connectAction() {
//        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
//
//            @Override
//            protected Boolean doInBackground(Void... arg0) {
        try {
//                    conn1.setReplyTimeout(60000);
//                    conn1.setPacketReplyTimeout(60000);
            conn1.connect();
//                    accountCreation();
            Log.e("Conencted", "Suceesfully");
        } catch (ConnectException e){
            Log.e("ConnectException",e.getCause() + e.getMessage());
        }catch (SmackException e1) {
            Log.e("Conencted", "SmackException" + e1.getMessage() + e1.getCause());
            e1.printStackTrace();
        } catch (IOException e1) {
            Log.e("Conencted", "IOException" + e1.getMessage() + e1.getCause());
            e1.printStackTrace();
        } catch (XMPPException e1) {
            Log.e("Conencted", "XMPPException" + e1.getMessage() + e1.getCause());
            e1.printStackTrace();
        } catch (InterruptedException e1) {
            Log.e("Conencted", "InterruptedException" + e1.getMessage() + e1.getCause());
            e1.printStackTrace();
        }
//                return null;
//            }
//        };
//        connectionThread.execute();

    }

//    private void accountCreation() {
//        try {
//            AccountManager accountManager = AccountManager.getInstance(conn1);
//            accountManager.sensitiveOperationOverInsecureConnection(true);
//            Map<String, String> additionalAttributes = new HashMap<>();
//            additionalAttributes.put("name", "Smack Integration Test");
//            additionalAttributes.put("email", "flow@igniterealtime.org");
//            accountManager.createAccount(Localpart.from("anki1514"), "anki1514");//,additionalAttributes);
//            Log.e("accountCreated", "created");
//        } catch (SmackException.NoResponseException e) {
//            Log.e("accountCreated", "NoResponseException" + e.getMessage() + e.getCause());
//            e.printStackTrace();
//        } catch (XMPPException.XMPPErrorException e) {
//            Log.e("accountCreated", "XMPPErrorException   " + e.getMessage() + e.getCause());
//            e.printStackTrace();
//        } catch (SmackException.NotConnectedException e) {
//            Log.e("accountCreated", "NotConnectedException" + e.getMessage() + e.getCause());
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            Log.e("accountCreated", "InterruptedException" + e.getMessage() + e.getCause());
//            e.printStackTrace();
//        } catch (XmppStringprepException e) {
//            Log.e("accountCreated", "XmppStringprepException" + e.getMessage() + e.getCause());
//            e.printStackTrace();
//        }
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            @Override
            public void run() {
                conn1.disconnect();
            }
        });
    }
}
