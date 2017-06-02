package smackconnection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


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
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;

import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by ankita on 25/5/17.
 */

/**
 * Important Links Android
<<<<<<< HEAD
 * https://github.com/igniterealtime/Smack
 */

public class SmackChatting extends AppCompatActivity {

    private static final String DOMAIN = "localhost";
    private static final String HOST = "192.168.3.30";
    Button connectButton, deleteButton;
    AbstractXMPPConnection conn1;
    AccountManager accountManager;
=======
 * 1) https://github.com/igniterealtime/Smack
 */
public class SmackChatting  extends AppCompatActivity {
    AbstractXMPPConnection conn1;
    private static final String DOMAIN = "nimbuzz.com";
    private static final String HOST = "o.nimbuzz.com";

>>>>>>> 7651f61bc50d1e64a7ce80d9eedbf9869a9b4985

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smack_activity);
<<<<<<< HEAD
        connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLoginTask task = new MyLoginTask();
                task.execute("");
            }
        });
       /* deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    accountManager.deleteAccount();
                    Log.e("Account", "Deleted");
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });*/

    }

    private void initAccount() {
        accountManager = AccountManager.getInstance(conn1);
        accountManager.sensitiveOperationOverInsecureConnection(true);
//        Map<String, String> additionalAttributes = new HashMap<>();
//        additionalAttributes.put("name", "Ankita Singh");
//        additionalAttributes.put("email", "ankita.singh@oodlestechnologies.com");
=======
        MyLoginTask task = new MyLoginTask();
        task.execute("");
>>>>>>> 7651f61bc50d1e64a7ce80d9eedbf9869a9b4985
    }

    private class MyLoginTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                DomainBareJid serviceNamJid = JidCreate.domainBareFrom("localhost");
                XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                        .setUsernameAndPassword("ankita", "774784")
                        .setHostAddress(InetAddress.getByName("192.168.3.30"))
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setXmppDomain(serviceNamJid)
                        .setPort(5222)
                        .setDebuggerEnabled(true) // to view what's happening in detail
                        .build();
                conn1 = new XMPPTCPConnection(config);
                try {
                    conn1.connect();
                    Log.e("completed", "completed");
                    if (conn1.isConnected()) {
                        Log.e("AndroidConnection", "completed");
                        Log.w("app", "conn done");
                        //accountCreation(conn1);
                         conn1.login();
                        // sentChatMessage("Hi Shashwat!");   //accountCreation(conn1);
                    }
                    if (conn1.isAuthenticated()) {
                        Log.e("AndroidConnection", "isAuthenticated");
                        roasterAndroid();
                        Log.w("app", "Auth done");
                    }
                } catch (Exception e) {
                    Log.w("app", e.toString());
                }
            } catch (Exception e) {
                Log.e("Exception", e.toString());

            }
            return "";
        }


        @Override
        protected void onPostExecute(String result) {

        }
    }

    private void roasterAndroid() {
        Roster roster = Roster.getInstanceFor(conn1);
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
            Log.e("AndroidSmack ", entry.toString());
        }
    }

    /*Message sending code after making connection with server */


    private void sentChatMessage(String messageStr) {
        ChatManager chatManager = ChatManager.getInstanceFor(conn1);
        chatManager.addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                System.out.println("New message from " + from + ": " + message.getBody());
            }
        });
        chatManager.addOutgoingListener(new OutgoingChatMessageListener() {
            @Override
            public void newOutgoingMessage(EntityBareJid to, Message message, Chat chat) {
                System.out.println("New message to " + to + ": " + message.getBody() + chat.getXmppAddressOfChatPartner());
            }
        });
        EntityBareJid jid = null;
        
        try {
            jid = JidCreate.entityBareFrom("shubham@localhost");     /*Give id to whom you want to chat*/
            Chat chat = chatManager.chatWith(jid);
            chat.send(messageStr);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.e("XmppStringprepException", e.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("InterruptedException", e.toString());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.e("NotConnectedException", e.toString());
        }
    }

    /*Account creation code...//change from server side deny all to allow all*/

    private void accountCreation(AbstractXMPPConnection conn1) {
        try {
            initAccount();
            accountManager.createAccount(Localpart.from("ankita38"), "anki1514");            //,additionalAttributes);
            Log.e("accountCreated", "created");
//            conn1.login("ankita12","anki1514");
//            if (conn1.isAuthenticated()) {
//                Log.e("AndroidConnection", "isAuthenticated");
//                roasterAndroid();
//                Log.w("app", "Auth done");
//            }
        } catch (Exception e) {
            Log.e("ExceptionException", e.toString());
        }
    }

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
