package com.example.demo8;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.activation.MimetypesFileTypeMap;
import javax.mail.*;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        DataBaseStuff.loadDB();
        Stage primaryStage=new Stage();
        Entering.signup(primaryStage);
    }
    public static void main(String[] args) {
        launch();
    }
}
class Theme{
    static boolean isDark=false;
}
class SearchUser extends Application
{
    public static AppUser selUser=null;
    TableView<NewUser> usersTable=new TableView<>();
    public static Scene searchUsersScene;
    ObservableList<NewUser> data=FXCollections.observableArrayList();
    public static void main(String[] args)
    {
        launch(args);
    }
    public void start(Stage stage)
    {
        boolean[] flag={false};
        Scene scene=new Scene(new Group());
        stage.setTitle("OOP Messenger");
        String accType;
        for(AppUser i:DataBaseStuff.appUsers){
            accType="Regular";
            if(i.isBusiness){
                accType="Business";
            }
            data.add(new NewUser(i.name,i.username,i.emailAddress,accType));
        }
        Label label = new Label("Users Search");
        label.setFont(new Font("Times New Roman", 20));
        usersTable.setEditable(true);
        TableColumn nameCol = new TableColumn("Name");
        nameCol.setMinWidth(100);
        nameCol.setCellValueFactory(new PropertyValueFactory<NewUser, String>("name"));
        TableColumn usernameCol = new TableColumn("Username");
        usernameCol.setMinWidth(100);
        usernameCol.setCellValueFactory(new PropertyValueFactory<NewUser, String>("username"));
        TableColumn emailCol = new TableColumn("Email");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(new PropertyValueFactory<NewUser, String>("email"));
        TableColumn typeCol = new TableColumn("Account Type");
        typeCol.setMinWidth(100);
        typeCol.setCellValueFactory(new PropertyValueFactory<NewUser, String>("accountType"));
        FilteredList<NewUser> flUser=new FilteredList(data, p -> true);
        usersTable.setItems(flUser);//Set the table's items using the filtered list
        usersTable.getColumns().addAll(nameCol, usernameCol, emailCol,typeCol);
        //Adding ChoiceBox and TextField here!
        ChoiceBox<String> choiceBox = new ChoiceBox();
        choiceBox.getItems().addAll("Name", "Username", "Email");
        choiceBox.setValue("Name");
        TextField textField = new TextField();
        textField.setPromptText("Search here!");
        textField.textProperty().addListener((obs, oldValue, newValue) -> {
            switch (choiceBox.getValue())//Switch on choiceBox value
            {
                case "Name":
                    flUser.setPredicate(p -> p.getName().toLowerCase().contains(newValue.toLowerCase().trim()));//filter table by first name
                    break;
                case "Username":
                    flUser.setPredicate(p -> p.getUsername().toLowerCase().contains(newValue.toLowerCase().trim()));//filter table by last name
                    break;
                case "Email":
                    flUser.setPredicate(p -> p.getEmail().toLowerCase().contains(newValue.toLowerCase().trim()));//filter table by email
                    break;
            }
        });
        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal)
                -> {//reset table and textfield when new choice is selected
            if (newVal != null) {
                textField.setText("");
            }
        });
        HBox hBox = new HBox(choiceBox, textField);//Add choiceBox and textField to hBox
        hBox.setAlignment(Pos.CENTER);//Center HBox
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        Button close=new Button("Close");
        close.setOnAction(e-> stage.close());
        vbox.getChildren().addAll(new StackPane(label),usersTable, hBox,new StackPane(close));
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        usersTable.setOnMouseClicked((EventHandler<Event>) event1 -> {
            NewUser selectedUser=usersTable.getSelectionModel().getSelectedItem();
            for(AppUser i:DataBaseStuff.appUsers){
                if(i.username.equals(selectedUser.getUsername())){
                    selUser=i;
                    Inside.setSelectedUser(i);
                    try {
                        Inside.showProfile(Inside.currentUser,selUser,stage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        });
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }

        stage.setResizable(false);
        searchUsersScene=scene;
        stage.setScene(scene);
        stage.show();
    }
    public static void setSelectedUser(AppUser selectedUser){
        Inside.setSelectedUser(selectedUser);
    }
    public static class NewUser{
        private final SimpleStringProperty name;
        private final SimpleStringProperty username;
        private final SimpleStringProperty email;
        private final SimpleStringProperty accountType;
        NewUser(String name,String username,String email,String accountType){
            this.name=new SimpleStringProperty(name);
            this.username=new SimpleStringProperty(username);
            this.email=new SimpleStringProperty(email);
            this.accountType=new SimpleStringProperty(accountType);
        }
        public String getName() { return name.get();}
        public String getUsername() { return username.get();}
        public String getEmail() { return email.get();}
        public String getAccountType() { return accountType.get();}
    }
}
class DataBaseStuff{
    public static ArrayList<AppUser> appUsers=new ArrayList<>();
    public static ArrayList<AppMessage> appMessages=new ArrayList<>();
    public static ArrayList<AppGroup> appGroups=new ArrayList<>();
    public static ArrayList<AppPost> appPosts=new ArrayList<>();
    public static ArrayList<AppStory> appStories=new ArrayList<>();
    public static ArrayList<AppComment> appComments=new ArrayList<>();
    public static ArrayList<AppChat> appChats=new ArrayList<>();
    public static void loadDB(){
        try{
            Connection connection=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/OOPProject?autoReconnect=true&useSSL=false"
                    ,"root","MyNewPass");
            DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss");
            Statement statement=connection.createStatement();
            ResultSet usersSet=statement.executeQuery("select * from users");
            AppUser tempUser;
            while(usersSet.next()){
                tempUser=new AppUser(usersSet.getString(1),usersSet.getString(2),usersSet.getString(3),
                        usersSet.getString(4),usersSet.getString(5),usersSet.getString(6),
                        Integer.parseInt(usersSet.getString(7))==1,usersSet.getString(8),
                        usersSet.getString(9),usersSet.getString(10),usersSet.getString(11),
                        usersSet.getString(12),usersSet.getString(13),usersSet.getString(14),
                        usersSet.getString(15),usersSet.getString(16),
                        Integer.parseInt(usersSet.getString(17))==1,
                        Integer.parseInt(usersSet.getString(18))==1,usersSet.getString(19),
                        Integer.parseInt(usersSet.getString(20)),
                        Integer.parseInt(usersSet.getString(21))==1,
                        Integer.parseInt(usersSet.getString(22))==1,
                        LocalDateTime.parse(usersSet.getString(23),formatter));
                appUsers.add(tempUser);
            }
            ResultSet groupsSet=statement.executeQuery("select * from appgroups");
            AppGroup tempGroup;
            while(groupsSet.next()){
                tempGroup=new AppGroup(groupsSet.getString(1),groupsSet.getString(2),
                        Integer.parseInt(groupsSet.getString(3)),groupsSet.getString(4),
                        groupsSet.getString(5),groupsSet.getString(6),groupsSet.getString(7),
                        Integer.parseInt(groupsSet.getString(8)),Integer.parseInt(groupsSet.getString(9))==1);
                appGroups.add(tempGroup);
            }
            ResultSet messagesSet=statement.executeQuery("select * from messages");
            AppMessage tempMessage;
            while(messagesSet.next()){
                tempMessage=new AppMessage(messagesSet.getString(1),messagesSet.getString(2),
                        Integer.parseInt(messagesSet.getString(3)),Integer.parseInt(messagesSet.getString(4)),
                        Integer.parseInt(messagesSet.getString(5)),
                        LocalDateTime.parse(messagesSet.getString(6),formatter),
                        messagesSet.getString(7),messagesSet.getString(8),
                        Integer.parseInt(messagesSet.getString(9)),Integer.parseInt(messagesSet.getString(10)),
                        Integer.parseInt(messagesSet.getString(11))==1,
                        Integer.parseInt(messagesSet.getString(12))==1,messagesSet.getString(13));
                appMessages.add(tempMessage);
            }
            ResultSet commentsSet=statement.executeQuery("select * from comments");
            AppComment tempComment;
            while(commentsSet.next()){
                tempComment=new AppComment(commentsSet.getString(1),Integer.parseInt(commentsSet.getString(2)),
                        Integer.parseInt(commentsSet.getString(3)),Integer.parseInt(commentsSet.getString(4)),
                        Integer.parseInt(commentsSet.getString(5))==1,
                        LocalDateTime.parse(commentsSet.getString(6),formatter),
                        commentsSet.getString(7),commentsSet.getString(8));
                appComments.add(tempComment);
            }
            ResultSet postsSet=statement.executeQuery("select * from posts");
            AppPost tempPost;
            while(postsSet.next()){
                tempPost=new AppPost(Integer.parseInt(postsSet.getString(1))==1,
                        Integer.parseInt(postsSet.getString(2)),
                        Integer.parseInt(postsSet.getString(3)),postsSet.getString(4),
                        postsSet.getString(5),postsSet.getString(6),
                        postsSet.getString(7),postsSet.getString(8));
                appPosts.add(tempPost);
            }
            ResultSet storiesSet=statement.executeQuery("select * from stories");
            AppStory tempStory;
            while(storiesSet.next()){
                tempStory=new AppStory(Integer.parseInt(storiesSet.getString(1)),
                        Integer.parseInt(storiesSet.getString(2)),
                        Integer.parseInt(storiesSet.getString(3))==1,storiesSet.getString(4),
                        storiesSet.getString(5),
                        LocalDateTime.parse(storiesSet.getString(6),formatter),
                        storiesSet.getString(7));
                appStories.add(tempStory);
            }
            ResultSet chatsSet=statement.executeQuery("select * from chats");
            AppChat tempChat;
            while(chatsSet.next()){
                tempChat=new AppChat(chatsSet.getString(1),chatsSet.getString(2),
                        chatsSet.getString(3),Integer.parseInt(chatsSet.getString(4)),
                        Integer.parseInt(chatsSet.getString(5)),
                        Integer.parseInt(chatsSet.getString(6))==1);
                appChats.add(tempChat);
            }
            String[] result,result1;
            for(AppUser i:appUsers){

                result=i.followersString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.followers.add(appUsers.get(Integer.parseInt(s)-1));
                    }
                }
                result=i.followingsString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.followings.add(appUsers.get(Integer.parseInt(s)-1));
                    }
                }
                result=i.followRequestsString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.followRequests.add(appUsers.get(Integer.parseInt(s)-1));
                    }
                }
                result=i.blockedUsersString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.blockedUsers.add(appUsers.get(Integer.parseInt(s)-1));
                    }
                }
                result=i.pinnedChatsString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.pinnedChats.add(appChats.get(Integer.parseInt(s)-1));
                    }
                }
                result=i.chatsString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.chats.add(appChats.get(Integer.parseInt(s)-1));
                    }
                }
                result=i.postsString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.posts.add(appPosts.get(Integer.parseInt(s)-1));
                    }
                }
            }
            for(AppGroup i:appGroups){
                result=i.membersString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.members.add(appUsers.get(Integer.parseInt(s)-1));
                    }
                }
                result=i.bannedString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.bannedUsers.add(appUsers.get(Integer.parseInt(s)-1));
                    }
                }
                i.admin=appUsers.get(i.adminID-1);
            }
            for(AppPost i:appPosts){
                result=i.likesString.split(",");
                for(String s:result){
                    result1=s.split("=");
                    if(!result1[0].equals("")){
                        i.likesUsers.add(appUsers.get(Integer.parseInt(result1[0])-1));
                        i.likesTimes.add(LocalDateTime.parse(result1[1],formatter));
                        AppSeen newAppSeen=new AppSeen(appUsers.get(Integer.parseInt(result1[0])-1),
                                LocalDateTime.parse(result1[1],formatter));
                        i.likes.add(newAppSeen);
                    }
                }
                result=i.commentsString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.comments.add(appComments.get(Integer.parseInt(s)-1));
                    }
                }
                result=i.seensString.split(",");
                for(String s:result){
                    result1=s.split("=");
                    if(!result1[0].equals("")){
                        i.seenUsers.add(appUsers.get(Integer.parseInt(result1[0])-1));
                        i.seenTimes.add(LocalDateTime.parse(result1[1],formatter));
                        AppSeen newAppSeen=new AppSeen(appUsers.get(Integer.parseInt(result1[0])-1),
                                LocalDateTime.parse(result1[1],formatter));
                        i.seens.add(newAppSeen);
                    }
                }
                i.sender=appUsers.get(i.senderID-1);
            }
            for(AppStory i:appStories){
                result=i.seensString.split(",");
                for(String s:result){
                    result1=s.split("=");
                    if(!result1[0].equals("")){
                        i.seenUsers.add(appUsers.get(Integer.parseInt(result1[0])-1));
                        i.seenTimes.add(LocalDateTime.parse(result1[1],formatter));
                        AppSeen newAppSeen=new AppSeen(appUsers.get(Integer.parseInt(result1[0])-1),
                                LocalDateTime.parse(result1[1],formatter));
                        i.seens.add(newAppSeen);
                    }
                }
                i.sender=appUsers.get(i.senderID-1);
                if(!i.isDeleted){
                    i.sender.stories.add(i);
                }
            }
            for(AppComment i:appComments){
                result=i.upVotesString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.upVotes.add(appUsers.get(Integer.parseInt(s)-1));
                    }
                }
                result=i.downVotesString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.downVotes.add(appUsers.get(Integer.parseInt(s)-1));
                    }
                }
                i.sender=appUsers.get(i.senderID-1);
            }
            for(AppMessage i:appMessages){
                result=i.seenString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.seen.add(appUsers.get(Integer.parseInt(s)-1));
                    }
                }
                result=i.isDeletedForString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.isDeletedFor.add(appUsers.get(Integer.parseInt(s)-1));
                    }
                }
                i.sender=appUsers.get(i.senderID-1);
            }
            for(AppChat i:appChats){
                result=i.messagesString.split(",");
                for(String s:result){
                    if(!s.equals("")){
                        i.messages.add(appMessages.get(Integer.parseInt(s)-1));
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void saveDB() throws SQLException {
        Connection connection=DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/OOPProject?autoReconnect=true&useSSL=false","root","MyNewPass");
        Statement usersStatement= connection.createStatement();
        usersStatement.executeUpdate("DELETE FROM users");
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss");
        appChats=new ArrayList<>();
        for(AppUser i:appUsers){
            int isBusiness=0,twoStep=0,isPrivate=0,isDeactivated=0,isDark=0;
            String followers="",followings="",chats="",pinChats="",posts="",followRequests="",blocks="",lastSeen="";
            if(i.isBusiness){
                isBusiness=1;
            }
            if(i.twoStep){
                twoStep=1;
            }
            if(i.isPrivate){
                isPrivate=1;
            }
            if(i.isDeactivated){
                isDeactivated=1;
            }
            if(i.isDark){
                isDark=1;
            }
            for(AppUser j:i.followers){
                followers+=j.ID+",";
            }
            for(AppUser j:i.followings){
                followings+=j.ID+",";
            }
            for(AppChat j:i.pinnedChats){
                j.ID=appChats.size()+1;
                pinChats+=j.ID+",";
                appChats.add(j);
            }
            for(AppChat j:i.chats){
                j.ID=appChats.size()+1;
                chats+=j.ID+",";
                appChats.add(j);
            }
            for(AppPost j:i.posts){
                posts+=j.postID+",";
            }
            for(AppUser j:i.followRequests){
                followRequests+=j.ID+",";
            }
            for(AppUser j:i.blockedUsers){
                blocks+=j.ID+",";
            }
            if(i.lastSeen!=null){
                lastSeen=formatter.format(i.lastSeen);
            }
            int usersResultSet=usersStatement.executeUpdate("INSERT INTO users "+
                    "VALUES('"+i.name+"','"+i.username+"','"+i.password+"','"+i.securityQuestion+"','"+i.securityHint+
                    "','"+i.securityAnswer+"',"+isBusiness+",'"+i.filePath.replace("\\","\\\\")+
                    "','"+i.bio+"','"+followers+"','"+followings+"','"+chats+"','"+pinChats+"','"+posts+"','"+followRequests+
                    "','"+blocks+"',"+twoStep+","+isPrivate+",'"+i.emailAddress+"',"+i.ID+","+isDeactivated+","+isDark+"" +
                    ",'"+lastSeen+"');");
        }
        Statement groupsStatement= connection.createStatement();
        groupsStatement.executeUpdate("DELETE FROM appgroups");
        for(AppGroup i:appGroups){
            int adminID=0,isDeleted=0;
            String members="",bans="";
            if(i.isDeleted){
                isDeleted=1;
            }
            for(AppUser j:i.members){
                members+=j.ID+",";
            }
            for(AppUser j:i.bannedUsers){
                bans+=j.ID+",";
            }
            if(i.admin!=null){
                adminID=i.admin.ID;
            }
            int groupsResultSet=groupsStatement.executeUpdate("INSERT INTO appgroups "+
                    "VALUES('"+i.name+"','"+i.groupName+"',"+adminID+",'"+members+"','"+bans+
                    "','"+i.filePath.replace("\\","\\\\")+"','"+i.bio+"',"+i.ID+","+isDeleted+");");
        }
        Statement postsStatement= connection.createStatement();
        postsStatement.executeUpdate("DELETE FROM posts");
        for(AppPost i:appPosts){
            int isDeleted=0;
            String likes="",comments="",seens="";
            if(i.isDeleted){
                isDeleted=1;
            }
            for(AppSeen j:i.likes){
                likes+=j.userID+"="+formatter.format(j.time)+",";
            }
            for(AppComment j:i.comments){
                comments+=j.ID+",";
            }
            for(AppSeen j:i.seens){
                seens+=j.userID+"="+formatter.format(j.time)+",";
            }
            int postsResultSet=postsStatement.executeUpdate("INSERT INTO posts "+
                    "VALUES("+isDeleted+","+i.senderID+","+i.postID+",'"+i.caption+
                    "','"+i.filePath.replace("\\","\\\\")+"','"+likes+"','"+comments+
                    "','"+seens+"');");
        }
        Statement storiesStatement=connection.createStatement();
        storiesStatement.executeUpdate("DELETE FROM stories");
        for(AppStory i:appStories){
            int isDeleted=0;
            String seens="";
            if(i.isDeleted){
                isDeleted=1;
            }
            for(AppSeen j:i.seens){
                seens+=j.userID+"="+formatter.format(j.time)+",";
            }
            int storiesResultSet=storiesStatement.executeUpdate("INSERT INTO stories "+
                    "VALUES("+i.senderID+","+i.ID+","+isDeleted+",'"+i.filePath.replace("\\","\\\\")+
                    "','"+i.caption+"','"+formatter.format(i.sendTime)+"','"+seens+"');");
        }
        Statement commentsStatement= connection.createStatement();
        commentsStatement.executeUpdate("DELETE FROM comments");
        for(AppComment i:appComments){
            int isDeleted=0;
            String upVotes="",downVotes="";
            if(i.isDeleted){
                isDeleted=1;
            }
            for(AppUser j:i.upVotes){
                upVotes+=j.ID+",";
            }
            for(AppUser j:i.downVotes){
                downVotes+=j.ID+",";
            }
            int commentsResultSet=commentsStatement.executeUpdate("INSERT INTO comments "+
                    "VALUES('"+i.text+"',"+i.senderID+","+i.replyTo+","+i.ID+","+isDeleted+
                    ",'"+formatter.format(i.sendTime)+"','"+upVotes+"','"+downVotes+"');");
        }
        Statement messagesStatement= connection.createStatement();
        messagesStatement.executeUpdate("DELETE FROM messages");
        for(AppMessage i:appMessages){
            int hasBeenForwarded=0,isDeleted=0;
            String seens="",isDeletedFor="";
            if(i.isDeleted){
                isDeleted=1;
            }
            if(i.hasBeenForwarded){
                hasBeenForwarded=1;
            }
            for(AppUser j:i.seen){
                seens+=j.ID+",";
            }
            for(AppUser j:i.isDeletedFor){
                isDeletedFor+=j.ID+",";
            }
            int messagesResultSet=messagesStatement.executeUpdate("INSERT INTO messages "+
                    "VALUES('"+i.text+"','"+i.stringID+"',"+i.senderID+","+i.receiverID+","+i.ID+",'"+formatter.format(i.sendTime)+
                    "','"+seens+"','"+isDeletedFor+"',"+i.replyTo+","+i.forwardFrom+","+isDeleted+
                    ","+hasBeenForwarded+",'"+i.filePath.replace("\\","\\\\")+"');");
        }
        Statement chatsStatement=connection.createStatement();
        chatsStatement.executeUpdate("DELETE FROM chats");
        for(AppChat i:appChats){
            int isDeleted=0;
            String messages="";
            if(i.isDeleted){
                isDeleted=1;
            }
            for(AppMessage j:i.messages){
                messages+=j.ID+",";
            }
            int chatsResultSet=chatsStatement.executeUpdate("INSERT INTO chats "+
                    "VALUES('"+i.name+"','"+i.filePath.replace("\\","\\\\")+"','"+messages+"',"+i.chatID+","+i.ID+
                    ","+isDeleted+");");
        }
    }
}
class Entering{
    public static int menuNumber=1;
    static String[] securityQuestions={"In what city were you born?",
            "What is the name of your favorite pet?",
            "What is your mother's maiden name?"};
    public static VBox enteringVB;
    public static StackPane menu(Stage primaryStage){
        MenuBar menuBar = new MenuBar();
        Menu menu1 = new Menu("Sign Up");
        Menu menu2 = new Menu("Sign In");
        MenuItem signUp=new MenuItem("Create A New Account");
        MenuItem signIn=new MenuItem("Log Into An Already Existing Account");
        signUp.setOnAction(e -> {
            if(menuNumber==2){
                menuNumber=1;
                signup(primaryStage);
            }
        });
        signIn.setOnAction(e -> {
            if(menuNumber==1){
                menuNumber=2;
                login(primaryStage);
            }
        });
        menu1.getItems().add(signUp);
        menu2.getItems().add(signIn);
        menuBar.getMenus().add(menu1);
        menuBar.getMenus().add(menu2);
        return new StackPane(menuBar);
    }
    public static void signup(Stage primaryStage){
        StackPane menu=menu(primaryStage);
        primaryStage.setTitle("OOP Messenger!");
        Label usernameLabel=new Label("Please Enter Your Username"),
        passwordLabel=new Label("Please Enter Your Password"),passwordLabel2=new Label("Please Re-Enter Your Password"),
        answerLabel=new Label("Security Question's Answer"),hintLabel=new Label("Security Question's Hint"),
        emailLabel=new Label("Please Enter Your Email Address");
        Button signupButton=new Button("sign up");
        TextField tfUsername=new TextField(),tfEmail=new TextField(),
        tfAnswer=new TextField(),tfHint=new TextField();
        PasswordField pfPassword=new PasswordField(),pfPassword2=new PasswordField();
        tfAnswer.setPrefWidth(250);
        tfHint.setPrefWidth(150);
        pfPassword.setPrefWidth(180);
        pfPassword2.setPrefWidth(180);
        VBox answer=new VBox(10,answerLabel,tfAnswer),hint=new VBox(10,hintLabel,tfHint),
        passVB=new VBox(10,passwordLabel,pfPassword),passVB2=new VBox(10,passwordLabel2,pfPassword2);
        HBox secQHB=new HBox(10,answer,hint),passHB=new HBox(10,passVB,passVB2);
        signupButton.setStyle("-fx-focus-color: transparent;");
        RadioButton[] security=new RadioButton[3];
        ToggleGroup questions=new ToggleGroup();
        VBox secQVB=new VBox(5);
        for(int j=0;j<3;j++){
            security[j]=new RadioButton(securityQuestions[j]);
            security[j].setToggleGroup(questions);
            secQVB.getChildren().addAll(security[j]);
        }
        //    setSecurityQuestions.setSelected(false);
        signupButton.setOnAction(ActionEvent-> {
            int flag=1;
            if(tfUsername.getText().length()==0){
                Stage stage=Alerts.Alert("Error!","You Should Enter A Username!");
                stage.show();
            }
            else{
                for(AppUser i:DataBaseStuff.appUsers){
                    if(i.username.equals(tfUsername.getText())){
                        flag=0;
                    }
                }
                if(flag==0){
                    Stage stage=Alerts.Alert("Error!","This Username Already Exists!");
                    stage.show();
                }
                else if(flag==1){
                    if(pfPassword.getText().length()<8){
                        Stage stage=Alerts.Alert("Error!","You Should Enter A Password With At Least 8 Characters!");
                        stage.show();
                    }
                    else{
                        if(pfPassword.getText().equals(pfPassword2.getText())){
                            int t=-1;
                            for(int k=0;k<3;k++){
                                if(security[k].isSelected()){
                                    if(!tfAnswer.getText().equals("")){
                                        if(!tfHint.getText().equals("")){
                                            if(!tfEmail.getText().equals("") &&
                                                    Pattern.matches("[a-zA-Z0-9.]+@[a-zA-Z]+.com",tfEmail.getText())){
                                                AppUser newUser=new AppUser(tfUsername.getText(),pfPassword.getText(),
                                                        securityQuestions[k],tfHint.getText(),tfAnswer.getText(),tfEmail.getText()
                                                ,DataBaseStuff.appUsers.size()+1);
                                                //AppChat savedMessages=new AppChat();
                                                //savedMessages.filePath="C:\\OOP File DataBase\\savedMessagesIcon.jpg";
                                                //newUser.pinnedChats.add(savedMessages);
                                                //savedMessages.name="Saved Messages";
                                                DataBaseStuff.appUsers.add(newUser);
                                                primaryStage.close();
                                                Inside.actualCurrentUser=newUser;
                                                Inside.selectAccountType(newUser);
                                            }
                                            else{
                                                Stage stage=Alerts.Alert("Error!","You Should Enter A Valid Email Address!");
                                                stage.show();
                                            }
                                        }
                                        else{
                                            Stage stage=Alerts.Alert("Error!","You Should Consider A Hint to the Question!");
                                            stage.show();
                                        }
                                    }
                                    else{
                                        Stage stage=Alerts.Alert("Error!","You Should Answer the Question!");
                                        stage.show();
                                    }
                                    t=k;
                                    break;
                                }
                            }
                            if(t==-1){
                                Stage stage=Alerts.Alert("Error!","You Should Choose A Security Question!");
                                stage.show();
                            }
                        }
                        else{
                            Stage stage=Alerts.Alert("Error!","The Entered Password are Different!");
                            stage.show();
                        }
                    }
                }
            }
        });
        VBox vbox = new VBox(10,menu,new StackPane(usernameLabel),tfUsername,new StackPane(passHB),
                new StackPane(secQVB),new StackPane(secQHB),new StackPane(emailLabel),tfEmail,new StackPane(signupButton));
        secQVB.setAlignment(Pos.CENTER);
        passHB.setAlignment(Pos.CENTER);
        enteringVB=vbox;
        Scene scene = new Scene(enteringVB,400,400);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static void login(Stage primaryStage){
        StackPane menu=menu(primaryStage);
        primaryStage.setTitle("OOP Messenger!");
        Label usernameLabel=new Label("Please Enter Your Username"),
                passwordLabel=new Label("Please Enter Your Password"),
                hintLabel=new Label("Security Question's Hint"),recoverLabel=new Label("Can't Remember Your Password? " +
                "Try One of the Following Ways:");
        RadioButton recover1=new RadioButton("Send the Password to This Username by Email"),
                recover2=new RadioButton("Sign In by Answering Your Security Question");
        ToggleGroup recoverGroup=new ToggleGroup();
        recover1.setToggleGroup(recoverGroup);
        recover2.setToggleGroup(recoverGroup);
        VBox recQVB=new VBox(5,recover1,recover2);
        Button signInButton=new Button("sign in");
        TextField tfUsername=new TextField();
        PasswordField pfPassword=new PasswordField();
        signInButton.setStyle("-fx-focus-color: transparent;");
        signInButton.setOnAction(ActionEvent-> {
            int flag=0;
            for(AppUser i:DataBaseStuff.appUsers){
                if(i.username.equals(tfUsername.getText())){
                    flag=1;
                    if(i.password.equals(pfPassword.getText())){
                        if(i.twoStep) {
                            AppEmail email=new AppEmail(i.username,i.emailAddress,i.password);
                            try {
                                email.sendMail(1);
                                Stage stage=new Stage();
                                VBox vBox1=new VBox(10);
                                stage.setTitle("2nd Step of Verification");
                                TextField tf1=new TextField();
                                Label l1=new Label("Please Enter the Code Sent To Your Email"),
                                        l2=new Label("");
                                l2.setTextFill(Color.web(String.valueOf(Color.RED)));
                                Button button1=new Button("Close"),b2=new Button("Submit");
                                StackPane sp1=new StackPane(button1),sp2=new StackPane(b2);
                                HBox hb=new HBox(10,sp2,sp1);
                                button1.setOnAction(ActionEvent1-> stage.close());
                                b2.setOnAction(ActionEvent1-> {
                                    if(tf1.getText().equals(AppEmail.sentCode)){
                                        stage.close();
                                        primaryStage.close();
                                        try {
                                            Inside.actualCurrentUser=i;
                                            Inside.showNeutralCurrUserPage(primaryStage,i,new int[]{0});
                                            //Inside.updateCurrUserVB(primaryStage,i);
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    else{
                                        l2.setText("You Have Entered The Code Wrong, Try Again!");
                                    }
                                });
                                vBox1.getChildren().addAll(new StackPane(l1),new StackPane(tf1),new StackPane(l2),
                                        new StackPane(hb));
                                //vBox1.setAlignment(Pos.CENTER);
                                tf1.setPrefWidth(200);
                                hb.setAlignment(Pos.CENTER);
                                stage.setScene(new Scene(vBox1, 300,200));
                                stage.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            primaryStage.close();
                            try {
                                Inside.actualCurrentUser=i;
                                Inside.showNeutralCurrUserPage(primaryStage,i,new int[]{0});
                                //Inside.updateCurrUserVB(primaryStage,i);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else{
                        if(recover1.isSelected()){
                            AppEmail email=new AppEmail(i.username,i.emailAddress,i.password);
                            try {
                                email.sendMail(2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            recover1.setSelected(false);
                        }
                        else if(recover2.isSelected()){
                            Stage stage=new Stage();
                            VBox vBox1=new VBox(10);
                            stage.setTitle("Answer Your Security Question To Sign In");
                            TextField tf1=new TextField();
                            Label l1=new Label(i.securityQuestion),l2=new Label("Hint: "+i.securityHint),
                            l3=new Label("Please Enter Your Answer:"), l4=new Label("");
                            l4.setTextFill(Color.web(String.valueOf(Color.RED)));
                            Button button1=new Button("Close"),b2=new Button("Submit");
                            StackPane sp1=new StackPane(button1),sp2=new StackPane(b2);
                            HBox hb=new HBox(10,sp2,sp1);
                            button1.setOnAction(ActionEvent1-> stage.close());
                            b2.setOnAction(ActionEvent1-> {
                                if(tf1.getText().equals(i.securityAnswer)){
                                    stage.close();
                                    primaryStage.close();
                                    Theme.isDark=i.isDark;
                                    try {
                                        Inside.actualCurrentUser=i;
                                        Inside.showNeutralCurrUserPage(primaryStage,i,new int[]{0});
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else{
                                    l4.setText("Your Answer is Wrong, Try Again!");
                                }
                            });
                            VBox vb=new VBox(5,l1,l2,l3);
                            vBox1.getChildren().addAll(new StackPane(vb),new StackPane(tf1),new StackPane(l4),
                                    new StackPane(hb));
                            vb.setAlignment(Pos.CENTER);
                            tf1.setPrefWidth(200);
                            hb.setAlignment(Pos.CENTER);
                            stage.setScene(new Scene(vBox1, 300,200));
                            stage.show();
                            recover2.setSelected(false);
                        }
                    }
                    break;
                }
            }
            if(flag==0){
                Stage stage=Alerts.Alert("Error!","There is No User With This Username!");
                stage.show();
            }
        });
        VBox vbox=new VBox(10,menu,new StackPane(usernameLabel),tfUsername,new StackPane(passwordLabel),
                pfPassword,new StackPane(recoverLabel),new StackPane(recQVB),new StackPane(signInButton));
        recQVB.setAlignment(Pos.CENTER);
        enteringVB=vbox;
        Scene scene = new Scene(enteringVB,400,400);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
class Inside{
    public static AppChat newGroupChat;
    public static Stage actualPrimaryStage;
    public static AppUser actualCurrentUser,currentUser,selUser;
    public static boolean strangers=false,addSeen=true;
    public static VBox currUserVB,currUserFollowingsVB,currUserChatsVB,currUserInsideChatVB,commentsLikesVB;
    static String[] securityQuestions={"In what city were you born?",
            "What is the name of your favorite pet?",
            "What is your mother's maiden name?",
            "What high school did you attend?",
            "What was the name of your elementary school?",
            "What was the make of your first car?",
            "What was your favorite food as a child?"};
    public static void selectAccountType(AppUser currUser){
        Stage stage=new Stage();
        VBox vBox1=new VBox(10);
        stage.setTitle("Choosing Account Type");
        Label l1=new Label("Please Choose One of the Proceeding Options:"),l2=new Label("");
        l2.setTextFill(Color.web(String.valueOf(Color.RED)));
        Button button1=new Button("Close"),b2=new Button("Submit");
        StackPane sp1=new StackPane(button1),sp2=new StackPane(b2);
        RadioButton business=new RadioButton("Business Account"),
                regular=new RadioButton("Regular Account");
        ToggleGroup accountType=new ToggleGroup();
        business.setToggleGroup(accountType);
        regular.setToggleGroup(accountType);
        HBox hb=new HBox(10,sp2,sp1),hb1=new HBox(10,regular,business);
        button1.setOnAction(ActionEvent1-> stage.close());
        Theme.isDark= currUser.isDark;
        b2.setOnAction(ActionEvent1-> {
            if(business.isSelected()){
                stage.close();
                currUser.isBusiness=true;
                try {
                    Stage primaryStage=new Stage();
                    Inside.showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else if(regular.isSelected()){
                stage.close();
                currUser.isBusiness=false;
                try {
                    Stage primaryStage=new Stage();
                    Inside.showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else{
                l2.setText("You Haven't Chosen Your Account Type Yet!");
            }
        });
        vBox1.getChildren().addAll(new StackPane(l1),new StackPane(hb1),new StackPane(l2),
                new StackPane(hb));
        vBox1.setAlignment(Pos.CENTER);
        hb.setAlignment(Pos.CENTER);
        hb1.setAlignment(Pos.CENTER);
        stage.setScene(new Scene(vBox1, 300,200));
        stage.show();
    }
    public static void showNeutralCurrUserPage(Stage primaryStage,AppUser currUser,int[] filterMode) throws FileNotFoundException {
        updateCurrUserVB(primaryStage, currUser);
        if(strangers){
            updateCurrUserFollowingsVBWithStrangers(primaryStage,currUser);
        }
        else{
            updateCurrUserFollowingsVBWithFollowings(primaryStage,currUser);
        }
        updateCurrUserNeutralMessagesVB(primaryStage,currUser,filterMode,new int[]{0});
        Scene scene = new Scene(new HBox(2,currUserChatsVB,currUserFollowingsVB,currUserVB));
        primaryStage.setTitle("OOP Messenger");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }
        primaryStage.show();
    }
    public static void showInsideNotChat(Stage primaryStage,AppUser currUser,int[] filterMode) throws FileNotFoundException {
        updateCurrUserNeutralMessagesVB(primaryStage,currUser,filterMode,new int[]{0});
        Scene scene = new Scene(new HBox(2,currUserChatsVB,currUserFollowingsVB,currUserVB));
        primaryStage.setTitle("OOP Messenger");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }
        primaryStage.show();
    }
    public static void showInsideChat(Stage primaryStage) throws FileNotFoundException {
        Scene scene = new Scene(new HBox(2,currUserChatsVB,currUserInsideChatVB));
        primaryStage.setTitle("OOP Messenger");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }
        primaryStage.show();
    }
    public static void sortChats(ArrayList<AppChat> chats){

        for (int i=0;i<chats.size()-1;++i){
            for(int j=0;j<chats.size()-i-1;++j){
                if(chats.get(j+1).messages.get(chats.get(j+1).messages.size()-1).sendTime.isAfter(
                        chats.get(j).messages.get(chats.get(j).messages.size()-1).sendTime)){
                    Collections.swap(chats,j+1,j);
                }
            }
        }
    }
    public static void updateCurrUserNeutralMessagesVB(Stage primaryStage, AppUser currUser,
                                                       int[] filterMode,int[] forwardFrom) throws FileNotFoundException {
        MenuBar menuBar = new MenuBar();
        Menu menu1=new Menu("Create New Chat");
        Menu menu2=new Menu("Filter Chats");
        Menu menu3=new Menu("Twitter Mode");
        MenuItem menu1Item1=new MenuItem("Create Group");
        MenuItem menu1Item2=new MenuItem("Send A Direct Message");
        MenuItem menu2Item1=new MenuItem("View All Chats");
        MenuItem menu2Item2=new MenuItem("View Groups");
        MenuItem menu2Item3=new MenuItem("View Direct Messages");
        MenuItem menu2Item4=new MenuItem("View Chats With Unseen Messages");
        MenuItem menu3Item1=new MenuItem("Enter");
        menu1Item1.setOnAction(e -> {
            try {
                newGroupChat=null;
                createGroup(primaryStage,currUser,filterMode,forwardFrom);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }

        });
        menu1Item2.setOnAction(e -> {
            actualPrimaryStage=primaryStage;
            currentUser=currUser;
            SearchUser SU=new SearchUser();
            SU.start(new Stage());
        });
        menu2Item1.setOnAction(e -> {
            if(filterMode[0]!=0){
                try {
                    showInsideNotChat(primaryStage,currUser, new int[]{0});
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
        menu2Item2.setOnAction(e -> {
            if(filterMode[0]!=1){
                try {
                    showInsideNotChat(primaryStage,currUser, new int[]{1});
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
        menu2Item3.setOnAction(e -> {
            if(filterMode[0]!=2){
                try {
                    showInsideNotChat(primaryStage,currUser, new int[]{2});
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
        menu2Item4.setOnAction(e -> {
            if(filterMode[0]!=3){
                try {
                    showInsideNotChat(primaryStage,currUser, new int[]{3});
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
        menu3Item1.setOnAction(e -> {
            try {
                showInsideNotChat(primaryStage,currUser,new int[]{0});
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        menu1.getItems().add(menu1Item1);
        menu1.getItems().add(menu1Item2);
        menu2.getItems().add(menu2Item1);
        menu2.getItems().add(menu2Item2);
        menu2.getItems().add(menu2Item3);
        menu2.getItems().add(menu2Item4);
        menu3.getItems().add(menu3Item1);
        menuBar.getMenus().add(menu1);
        menuBar.getMenus().add(menu2);
        menuBar.getMenus().add(menu3);


        ScrollPane ScrP = new ScrollPane();
        int currUserFollowingsVBMinWidth = 350, currUserFollowingsVBMinHeight=550;
        ScrP.setMinSize(currUserFollowingsVBMinWidth, currUserFollowingsVBMinHeight);
        ScrP.setMaxSize(currUserFollowingsVBMinWidth, currUserFollowingsVBMinHeight);
        VBox chatsVB=new VBox();
        ArrayList<AppChat> currUserChats=new ArrayList<>();
        String chatHeaderPath;
        InputStream chatHeaderStream;
        Image chatHeaderImage;
        Circle chatHeaderCircle;
        ImagePattern chatHeaderPattern;
        Label chatHeaderLabel,chatFooterLabel;
        Text chatHeaderText;
        HBox chatHeaderHB;
        long seconds,minutes,hours,days,weeks;
        for(AppChat i:currUser.pinnedChats){
            if(filterMode[0]==0){
                currUserChats.add(i);
            }
            else if(filterMode[0]==1){
                if(i.chatID<0){
                    currUserChats.add(i);
                }
            }
            else if(filterMode[0]==2){
                if(0<i.chatID){
                    currUserChats.add(i);
                }
            }
            else if(filterMode[0]==3){
                if(i.messages.size()!=0) {
                    if(!i.messages.get(i.messages.size()-1).seen.contains(currUser)) {
                        currUserChats.add(i);
                    }
                }
            }
        }
        sortChats(currUser.chats);
        for(AppChat i:currUser.chats){
            if(filterMode[0]==0){
                currUserChats.add(i);
            }
            else if(filterMode[0]==1){
                if(i.chatID<0){
                    currUserChats.add(i);
                }
            }
            else if(filterMode[0]==2){
                if(0<i.chatID){
                    currUserChats.add(i);
                }
            }
            else if(filterMode[0]==3){
                if(i.messages.size()!=0) {
                    if(!i.messages.get(i.messages.size()-1).seen.contains(currUser)) {
                        currUserChats.add(i);
                    }
                }
            }
        }
        HBox[] chatsHBs=new HBox[currUserChats.size()];
        int a=0;
        for(AppChat i:currUserChats){
            chatHeaderPath="C:\\OOP File DataBase\\neutralProfile.jpg";
            if(i.chatID<0){
                for(AppGroup j:DataBaseStuff.appGroups){
                    if(j.ID==i.chatID){
                        i.filePath=j.filePath;
                    }
                }
            }
            else if(i.chatID!=currUser.ID){
                for(AppUser j:DataBaseStuff.appUsers){
                    if(j.ID==i.chatID){
                        i.filePath=j.filePath;
                    }
                }
            }
            else{
                i.filePath="C:\\OOP File DataBase\\savedMessagesIcon.jpg";
            }
            if(!i.filePath.equals("")){
                chatHeaderPath=i.filePath;
            }
            chatHeaderStream=new FileInputStream(chatHeaderPath);
            chatHeaderImage=new Image(chatHeaderStream);
            chatHeaderCircle=new Circle(20);
            chatHeaderPattern=new ImagePattern(chatHeaderImage);
            chatHeaderCircle.setFill(chatHeaderPattern);
            chatHeaderText=new Text(i.name);
            chatHeaderLabel=new Label(i.name);
            String s="",timeString="";
            if(i.messages.size()!=0){
                if(i.messages.get(i.messages.size()-1).isDeleted || i.messages.get(i.messages.size()-1).isDeletedFor.contains(currUser)){
                    s="*Message Was Deleted*";
                }
                else{
                    s=i.messages.get(i.messages.size()-1).sender.username+": "+i.messages.get(i.messages.size()-1).text;
                }
                seconds=ChronoUnit.SECONDS.between(i.messages.get(i.messages.size()-1).sendTime,LocalDateTime.now());
                minutes=ChronoUnit.MINUTES.between(i.messages.get(i.messages.size()-1).sendTime,LocalDateTime.now());
                hours=ChronoUnit.HOURS.between(i.messages.get(i.messages.size()-1).sendTime,LocalDateTime.now());
                days=ChronoUnit.DAYS.between(i.messages.get(i.messages.size()-1).sendTime,LocalDateTime.now());
                weeks=ChronoUnit.WEEKS.between(i.messages.get(i.messages.size()-1).sendTime,LocalDateTime.now());
                if(seconds<60){
                    timeString=seconds+" s";
                }
                else if(minutes<60){
                    timeString=minutes+" m";
                }
                else if(hours<24){
                    timeString=hours+" h";
                }
                else if(days<7){
                    timeString=days+" d";
                }
                else{
                    timeString=weeks+" w";
                }
            }
            Label timeLabel=new Label(timeString);
            chatFooterLabel=new Label(s);
            if(i.messages.size()!=0){
                if(!i.messages.get(i.messages.size()-1).seen.contains(currUser)){
                    chatFooterLabel.setFont(Font.font("Times new roman",15));
                }
            }
            chatFooterLabel.setMaxWidth(currUserFollowingsVBMinWidth-60);
            //chatHeaderHB=new HBox(5,chatHeaderCircle,chatHeaderLabel);
            chatHeaderHB=new HBox(currUserFollowingsVBMinWidth-105-chatHeaderText.getLayoutBounds().getWidth()
                    ,chatHeaderLabel,timeLabel);
            VBox chatVB=new VBox(3,chatHeaderHB,chatFooterLabel);
            HBox tempChatHB=new HBox(5,chatHeaderCircle,chatVB);
            String cssLayout = "-fx-border-color: black;\n" +
                    "-fx-border-insets: 1;\n" +
                    "-fx-border-width: 1;\n";
            tempChatHB.setStyle(cssLayout);
            tempChatHB.setMinSize(currUserFollowingsVBMinWidth-15,50);
            tempChatHB.setMaxSize(currUserFollowingsVBMinWidth-15,50);
            chatsHBs[a]=tempChatHB;
            a++;
        }
        if(currUserChats.size()!=0){
            for(int i=0;i<currUserChats.size(); i++) {
                int finalI = i;
                chatsHBs[i].addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                    long startTime;
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                            startTime = System.currentTimeMillis();
                        } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                            //show inside of chat
                            if(forwardFrom[0]==0){
                                if (500 < System.currentTimeMillis() - startTime) {
                                    Stage chatOptionsStage=new Stage();
                                    RadioButton o1=new RadioButton("Pin/UnPin"),o2=new RadioButton("Clear History"),
                                        o3=new RadioButton("Block/UnBlock"),o4=new RadioButton("Delete Chat");
                                    ToggleGroup options=new ToggleGroup();
                                    o1.setToggleGroup(options);
                                    o2.setToggleGroup(options);
                                    o3.setToggleGroup(options);
                                    o4.setToggleGroup(options);
                                    VBox optionVB=new VBox(5,o1,o2,o3,o4);
                                    options.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                                        public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
                                            RadioButton rb=(RadioButton)options.getSelectedToggle();
                                            if(rb!=null){
                                                if(rb.getText().equals("Pin/UnPin")){
                                                    if(currUser.pinnedChats.contains(currUserChats.get(finalI))){
                                                        currUser.pinnedChats.remove(currUserChats.get(finalI));
                                                        currUser.chats.add(0,currUserChats.get(finalI));
                                                    }
                                                    else{
                                                        currUser.chats.remove(currUserChats.get(finalI));
                                                        currUser.pinnedChats.add(currUserChats.get(finalI));
                                                    }
                                                    try {
                                                        updateCurrUserNeutralMessagesVB(primaryStage,currUser,
                                                                new int[]{0},new int[]{0});
                                                        updateInsideChatVB(primaryStage,currUser,currUserChats.get(finalI));
                                                        showInsideChat(primaryStage);
                                                    } catch (FileNotFoundException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                else if(rb.getText().equals("Clear History")){
                                                    for(AppMessage j:currUserChats.get(finalI).messages){
                                                        if(!j.isDeletedFor.contains(currUser)){
                                                            j.isDeletedFor.add(currUser);
                                                        }
                                                    }
                                                    try {
                                                        updateCurrUserNeutralMessagesVB(primaryStage,currUser,
                                                                new int[]{0},new int[]{0});
                                                        updateInsideChatVB(primaryStage,currUser,currUserChats.get(finalI));
                                                        showInsideChat(primaryStage);
                                                    } catch (FileNotFoundException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                else if(rb.getText().equals("Block/UnBlock")){
                                                    Stage tempAlert;
                                                    if(currUserChats.get(finalI).chatID<0){
                                                        tempAlert=Alerts.Alert("Error!","You Can Not Block A Group!");
                                                    }
                                                    else{
                                                        if(currUser.blockedUsers.contains(DataBaseStuff.appUsers.
                                                                get(currUserChats.get(finalI).chatID-1))){
                                                            currUser.blockedUsers.remove(DataBaseStuff.appUsers.
                                                                    get(currUserChats.get(finalI).chatID-1));
                                                            tempAlert=Alerts.Alert("Done!","User is UnBlocked!");
                                                        }
                                                        else{
                                                            currUser.blockedUsers.add(DataBaseStuff.appUsers.
                                                                    get(currUserChats.get(finalI).chatID-1));
                                                            tempAlert=Alerts.Alert("Done!","User is Blocked!");
                                                        }
                                                        try {
                                                            updateCurrUserNeutralMessagesVB(primaryStage,currUser,
                                                                    new int[]{0},new int[]{0});
                                                            updateInsideChatVB(primaryStage,currUser,currUserChats.get(finalI));
                                                            showInsideChat(primaryStage);
                                                        } catch (FileNotFoundException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    tempAlert.show();
                                                }
                                                else if(rb.getText().equals("Delete Chat")){
                                                    if(currUserChats.get(finalI).chatID<0){
                                                        DataBaseStuff.appGroups.get(-currUserChats.get(finalI).chatID-1)
                                                                .members.remove(currUser);
                                                    }
                                                    else{
                                                        for(AppMessage j:currUserChats.get(finalI).messages){
                                                            if(!j.isDeletedFor.contains(currUser)){
                                                                j.isDeletedFor.add(currUser);
                                                            }
                                                        }
                                                    }
                                                    if(currUser.pinnedChats.contains(currUserChats.get(finalI))){
                                                        currUser.pinnedChats.remove(currUserChats.get(finalI));
                                                    }
                                                    else{
                                                        currUser.chats.remove(currUserChats.get(finalI));
                                                    }
                                                    try {
                                                        showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
                                                    } catch (FileNotFoundException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                chatOptionsStage.close();
                                            }
                                        }
                                    });
                                    Scene optionsScene=new Scene(optionVB);
                                    int col=50;
                                    if(Theme.isDark){
                                        optionsScene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                                        optionsScene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
                                    }
                                    chatOptionsStage.setScene(optionsScene);
                                    chatOptionsStage.show();
                                }
                                else {
                                    try {
                                        updateInsideChatVB(primaryStage,currUser,currUserChats.get(finalI));
                                        updateCurrUserNeutralMessagesVB(primaryStage,currUser,new int[]{0},new int[]{0});
                                        showInsideChat(primaryStage);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            else{
                                int flag=0;
                                if(currUserChats.get(finalI).chatID<0){
                                    flag=2;
                                }
                                else{
                                    if(currUserChats.get(finalI).chatID==currUser.ID)
                                        flag=1;
                                }
                                sendChatMessage(primaryStage,currUser,DataBaseStuff.appMessages.get(forwardFrom[0]-1).text,
                                        DataBaseStuff.appMessages.get(forwardFrom[0]-1).filePath,forwardFrom[0],0,
                                        currUserChats.get(finalI),flag);
                                DataBaseStuff.appMessages.get(forwardFrom[0]-1).hasBeenForwarded=true;
                                try {
                                    updateCurrUserNeutralMessagesVB(primaryStage,currUser,
                                            new int[]{0},new int[]{0});
                                    updateInsideChatVB(primaryStage,currUser,currUserChats.get(finalI));
                                    showInsideChat(primaryStage);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                chatsVB.getChildren().add(new BorderPane(chatsHBs[i]));
            }
        }
        else{
            chatsVB=new VBox(new Label("No Chats With This Characteristic Yet!"));
        }
        ScrP.setContent(chatsVB);
        VBox finalVB=new VBox(menuBar,ScrP);
        currUserChatsVB=finalVB;
    }
    public static void createGroup(Stage primaryStage, AppUser currUser,
                                                       int[] filterMode,int[] forwardFrom) throws FileNotFoundException {
        try {
            Stage stage = new Stage();
            Label label = new Label(), titleLabel = new Label("Choose Other Group Members");
            ListView<String> listView = new ListView<>();
            ObservableList<String> list = FXCollections.observableArrayList();
            listView.setItems(list);
            Label nameIDLabel = new Label("Enter Group Name & ID"), bioLabel = new Label("Enter Group Description");
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            ArrayList<String>[] selectedItems = new ArrayList[]{new ArrayList<>()};
            listView.setOnMouseClicked(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    if (selectedItems[0].contains(listView.getSelectionModel().getSelectedItem())) {
                        selectedItems[0].remove(listView.getSelectionModel().getSelectedItem());
                    } else {
                        selectedItems[0].add(listView.getSelectionModel().getSelectedItem());
                    }
                    String f = "";
                    for (String s : selectedItems[0]) {
                        f += s + " ";
                    }
                    label.setText(f);
                }
            });
            TextField tfName = new TextField(), tfID = new TextField(), tfBio = new TextField();
            tfName.setPromptText("Enter The Group's Name Here!");
            tfID.setPromptText("Enter The Group's ID Here!");
            tfBio.setPromptText("Enter The Group's Description Here!");
            Button b = new Button("submit");
            for (AppUser i : currUser.followers) {
                list.add(i.username);
            }
            b.setOnAction(e -> {
                if (tfName.getText().length() == 0 || tfID.getText().length() == 0) {
                    nameIDLabel.setText("Group Has to Have Name & ID");
                } else {
                    boolean flag = true;
                    for (AppGroup i : DataBaseStuff.appGroups) {
                        if (tfID.getText().equals(i.groupName)) {
                            nameIDLabel.setText("Group With This ID Already Exists");
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        ArrayList<AppUser> members = new ArrayList<>();
                        members.add(currUser);
                        for (AppUser i : currUser.followers) {
                            if (selectedItems[0].contains(i.username)) {
                                members.add(i);
                            }
                        }
                        AppGroup newGroup=new AppGroup(currUser, tfName.getText(), tfID.getText(), members,
                                -DataBaseStuff.appGroups.size() - 1, tfBio.getText());
                        AppChat newChat=new AppChat();
                        newChat.chatID=newGroup.ID;
                        newChat.name=newGroup.name;
                        newChat.filePath=newGroup.filePath;
                        currUser.chats.add(newChat);
                        newGroupChat=newChat;
                        DataBaseStuff.appGroups.add(newGroup);
                        sendChatMessage(primaryStage, currUser, "I Have Created This Group",
                                "", 0, 0, newChat, 2);
                        stage.close();
                        if(newGroupChat!=null){
                            try {
                                updateCurrUserNeutralMessagesVB(primaryStage,currUser,
                                        new int[]{0},new int[]{0});
                                updateInsideChatVB(primaryStage,currUser,newGroupChat);
                                showInsideChat(primaryStage);
                            } catch (FileNotFoundException ex) {
                                ex.printStackTrace();
                            }

                        }
                    }
                }
            });
            int newGroupVBWidth = 300;
            label.setWrapText(true);
            label.setTextAlignment(TextAlignment.JUSTIFY);
            label.setMaxWidth(newGroupVBWidth);
            label.setMinHeight(50);
            HBox namesHB = new HBox(tfName, tfID);
            tfName.setMinWidth(newGroupVBWidth / 2);
            tfName.setMaxWidth(newGroupVBWidth / 2);
            tfID.setMinWidth(newGroupVBWidth / 2);
            tfID.setMaxWidth(newGroupVBWidth / 2);
            VBox fin = new VBox(5, nameIDLabel, namesHB, bioLabel, tfBio, titleLabel, listView, label, b);
            Scene scene = new Scene(fin);
            fin.setAlignment(Pos.CENTER);
            fin.setMinWidth(newGroupVBWidth);
            fin.setMaxWidth(newGroupVBWidth);
            int col=50;
            if(Theme.isDark){
                scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
            }
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void updateInsideChatVB(Stage primaryStage,AppUser currUser,AppChat currChat) throws FileNotFoundException {
        int[] replyTo = {0},forwardFrom={0};
        ArrayList<AppMessage> currMessages=new ArrayList<>();
        for(AppMessage i:currChat.messages){
            if(!i.isDeleted){
                if(!i.isDeletedFor.contains(currUser)){
                    currMessages.add(i);
                }
            }
        }
        String chatHeaderPath;
        InputStream chatHeaderStream;
        Image chatHeaderImage;
        Circle chatHeaderCircle;
        ImagePattern chatHeaderPattern;
        Label chatHeaderLabel,chatFooterLabel;
        Text chatHeaderText;
        HBox chatHeaderHB;
        long seconds,minutes,hours,days,weeks;
        //HBox[] chatsHBs=new HBox[currUserChats.size()];
        int a=0,insideChatVBWidth=600,insideChatVBHeight=600,maxMessageWidth=(insideChatVBWidth-20)/2+100;
        chatHeaderPath="C:\\OOP File DataBase\\neutralProfile.jpg";
        AppUser selectedUser=null;
        AppGroup selectedGroup=null;
        String infoString="";
        int flag=0;
        if(currChat.chatID<0){
            for(AppGroup i:DataBaseStuff.appGroups){
                if(i.ID==currChat.chatID){
                    selectedGroup=i;
                    flag=2;
                    break;
                }
            }
            if(selectedGroup!=null){
                currChat.filePath=selectedGroup.filePath;
                currChat.name=selectedGroup.name;
                infoString=selectedGroup.members.size()+" Member(s)";
            }
        }
        else if(0<currChat.chatID){
            if(currChat.chatID==currUser.ID){
                flag=1;
                selectedUser=currUser;
                currChat.filePath="C:\\OOP File DataBase\\savedMessagesIcon.jpg";
                currChat.name="Saved Messages";
            }
            else{
                for(AppUser i:DataBaseStuff.appUsers){
                    if(i.ID==currChat.chatID){
                        selectedUser=i;
                        break;
                    }
                }
                if(selectedUser!=null){
                    currChat.filePath=selectedUser.filePath;
                    currChat.name=selectedUser.username;
                    seconds=ChronoUnit.SECONDS.between(selectedUser.lastSeen,LocalDateTime.now());
                    minutes=ChronoUnit.MINUTES.between(selectedUser.lastSeen,LocalDateTime.now());
                    hours=ChronoUnit.HOURS.between(selectedUser.lastSeen,LocalDateTime.now());
                    days=ChronoUnit.DAYS.between(selectedUser.lastSeen,LocalDateTime.now());
                    weeks=ChronoUnit.WEEKS.between(selectedUser.lastSeen,LocalDateTime.now());
                    if(seconds<60){
                        infoString="Last Seen "+seconds+" s Ago";
                    }
                    else if(minutes<60){
                        infoString="Last Seen "+minutes+" m Ago";
                    }
                    else if(hours<24){
                        infoString="Last Seen "+hours+" h Ago";
                    }
                    else if(days<7){
                        infoString="Last Seen "+days+" d Ago";
                    }
                    else{
                        infoString="Last Seen "+weeks+" w Ago";
                    }
                    if(selectedUser.blockedUsers.contains(currUser)){
                        currChat.filePath="C:\\OOP File DataBase\\neutralProfile.jpg";
                        infoString="Last Seen A Long Time Ago";
                    }
                }
            }
        }
        chatHeaderPath=currChat.filePath;
        chatHeaderStream=new FileInputStream(chatHeaderPath);
        chatHeaderImage=new Image(chatHeaderStream);
        chatHeaderCircle=new Circle(20);
        chatHeaderPattern=new ImagePattern(chatHeaderImage);
        chatHeaderCircle.setFill(chatHeaderPattern);
        chatHeaderLabel=new Label(currChat.name);
        Label infoLabel=new Label(infoString);
        String s="",timeString="";
        VBox headerVB=new VBox(10,chatHeaderLabel,infoLabel);
        HBox finalHeaderHB=new HBox(5,chatHeaderCircle,headerVB);
        if(flag==1){
            finalHeaderHB=new HBox(5,chatHeaderCircle,chatHeaderLabel);
        }
        finalHeaderHB.setMinSize(insideChatVBWidth,45);
        finalHeaderHB.setMaxSize(insideChatVBWidth,45);
        ScrollPane ScrP=new ScrollPane();
        ScrP.setMinSize(insideChatVBWidth,insideChatVBHeight-102);
        ScrP.setMaxSize(insideChatVBWidth,insideChatVBHeight-102);
        if(currMessages.size()!=0){
            ArrayList<VBox> messagesVBs=new ArrayList<>();
            VBox tempMessage=new VBox(),messagesVB=new VBox(2);
            Label messageTextLabel,timeLabel;
            for(AppMessage i:currMessages){
                int messageSituation=0;
                if(!i.seen.contains(currUser)){
                    i.seen.add(currUser);
                }
                seconds=ChronoUnit.SECONDS.between(i.sendTime,LocalDateTime.now());
                minutes=ChronoUnit.MINUTES.between(i.sendTime,LocalDateTime.now());
                hours=ChronoUnit.HOURS.between(i.sendTime,LocalDateTime.now());
                days=ChronoUnit.DAYS.between(i.sendTime,LocalDateTime.now());
                weeks=ChronoUnit.WEEKS.between(i.sendTime,LocalDateTime.now());
                if(seconds<60){
                    timeString=seconds+" s";
                }
                else if(minutes<60){
                    timeString=minutes+" m";
                }
                else if(hours<24){
                    timeString=hours+" h";
                }
                else if(days<7){
                    timeString=days+" d";
                }
                else{
                    timeString=weeks+" w";
                }
                timeLabel=new Label(timeString);
                if(i.sender.equals(currUser)){
                    if(i.replyTo!=0){
                        if(!DataBaseStuff.appMessages.get(i.replyTo-1).isDeleted){
                            if(!DataBaseStuff.appMessages.get(i.replyTo-1).isDeletedFor.contains(currUser)){
                                messageSituation=1;
                            }
                        }
                    }
                    else if(i.forwardFrom!=0){
                        if(!DataBaseStuff.appMessages.get(i.forwardFrom-1).isDeleted){
                            messageSituation=2;
                        }
                        else{
                            messageSituation=-1;
                        }
                    }
                }
                else{
                    messageSituation=3;
                    if(i.replyTo!=0){
                        if(!DataBaseStuff.appMessages.get(i.replyTo-1).isDeleted){
                            if(!DataBaseStuff.appMessages.get(i.replyTo-1).isDeletedFor.contains(currUser)){
                                messageSituation=4;
                            }
                        }
                    }
                    else if(i.forwardFrom!=0){
                        if(!DataBaseStuff.appMessages.get(i.forwardFrom-1).isDeleted){
                            messageSituation=5;
                        }
                        else{
                            messageSituation=-1;
                        }
                    }
                }
                messageTextLabel=new Label(i.text);
                Text messageText=new Text(i.text);
                double messageWidth=messageText.getLayoutBounds().getWidth(),
                        finalMessageWidth=min(maxMessageWidth,messageWidth),finalFileWidth=max(finalMessageWidth,400);
                Rectangle tempRect=null;
                if(i.filePath.length()!=0){
                    if(-1<messageSituation){
                        messageSituation=messageSituation+6;
                        InputStream tempStream;
                        Image tempImage;
                        ImagePattern tempPattern;
                        tempStream=new FileInputStream(i.filePath);
                        tempImage=new Image(tempStream);
                        tempRect=new Rectangle(finalFileWidth,400);
                        tempPattern=new ImagePattern(tempImage);
                        tempRect.setFill(tempPattern);
                    }
                }
                if(messageSituation!=-1){
                    if(messageSituation==0){
                        finalMessageWidth=max(finalMessageWidth,200);
                        tempMessage=new VBox(2,messageTextLabel,timeLabel);
                        //messageTextLabel.setAlignment(Pos.CENTER_RIGHT);
                        tempMessage.setTranslateX(insideChatVBWidth-finalMessageWidth-20);
                        timeLabel.setTranslateX(finalMessageWidth-25);
                        timeLabel.setFont(Font.font(9));
                        tempMessage.setMinWidth(finalMessageWidth);
                        tempMessage.setMaxWidth(finalMessageWidth);
                        tempMessage.setMinHeight(30);
                        messageTextLabel.setWrapText(true);
                        messageTextLabel.setTextAlignment(TextAlignment.JUSTIFY);
                        messageTextLabel.setMaxWidth(finalMessageWidth);
                    }
                    else if(messageSituation==1){
                        finalMessageWidth=max(finalMessageWidth,200);
                        Label usernameLabel=new Label("Replying to "+DataBaseStuff.appMessages.get(i.replyTo-1).sender.username+": "+
                                DataBaseStuff.appMessages.get(i.replyTo-1).text);
                        usernameLabel.setMaxWidth(200);
                        usernameLabel.setFont(Font.font(10));
                        tempMessage=new VBox(2,usernameLabel,messageTextLabel,timeLabel);
                        //messageTextLabel.setAlignment(Pos.CENTER_RIGHT);
                        tempMessage.setTranslateX(insideChatVBWidth-finalMessageWidth-20);
                        timeLabel.setTranslateX(finalMessageWidth-25);
                        timeLabel.setFont(Font.font(9));
                        tempMessage.setMinWidth(finalMessageWidth);
                        tempMessage.setMaxWidth(finalMessageWidth);
                        tempMessage.setMinHeight(30);
                        messageTextLabel.setWrapText(true);
                        messageTextLabel.setTextAlignment(TextAlignment.JUSTIFY);
                        messageTextLabel.setMaxWidth(finalMessageWidth);
                    }
                    else if(messageSituation==2){
                        finalMessageWidth=max(finalMessageWidth,200);
                        Label usernameLabel=new Label("Forwarded From "+DataBaseStuff.appMessages.get(i.forwardFrom-1).sender.username);
                        usernameLabel.setMaxWidth(200);
                        usernameLabel.setFont(Font.font(10));
                        tempMessage=new VBox(2,usernameLabel,messageTextLabel,timeLabel);
                        //messageTextLabel.setAlignment(Pos.CENTER_RIGHT);
                        tempMessage.setTranslateX(insideChatVBWidth-finalMessageWidth-20);
                        timeLabel.setTranslateX(finalMessageWidth-25);
                        timeLabel.setFont(Font.font(9));
                        tempMessage.setMinWidth(finalMessageWidth);
                        tempMessage.setMaxWidth(finalMessageWidth);
                        tempMessage.setMinHeight(30);
                        messageTextLabel.setWrapText(true);
                        messageTextLabel.setTextAlignment(TextAlignment.JUSTIFY);
                        messageTextLabel.setMaxWidth(finalMessageWidth);
                    }
                    else if(messageSituation==3){
                        finalMessageWidth=max(finalMessageWidth,200);
                        Label usernameLabel=new Label(i.sender.username);
                        usernameLabel.setMaxWidth(200);
                        usernameLabel.setFont(Font.font(10));
                        tempMessage=new VBox(2,usernameLabel,messageTextLabel,timeLabel);
                        //messageTextLabel.setAlignment(Pos.CENTER_RIGHT);
                        tempMessage.setTranslateX(5);
                        timeLabel.setTranslateX(finalMessageWidth-25);
                        timeLabel.setFont(Font.font(9));
                        tempMessage.setMinWidth(finalMessageWidth);
                        tempMessage.setMaxWidth(finalMessageWidth);
                        tempMessage.setMinHeight(30);
                        messageTextLabel.setWrapText(true);
                        messageTextLabel.setTextAlignment(TextAlignment.JUSTIFY);
                        messageTextLabel.setMaxWidth(finalMessageWidth);
                    }
                    else if(messageSituation==4){
                        finalMessageWidth=max(finalMessageWidth,200);
                        Label usernameLabel=new Label(i.sender.username+" Replying to "+DataBaseStuff.appMessages.get(i.replyTo-1).
                                sender.username+": "+DataBaseStuff.appMessages.get(i.replyTo-1).text);
                        usernameLabel.setMaxWidth(200);
                        usernameLabel.setFont(Font.font(10));
                        tempMessage=new VBox(2,usernameLabel,messageTextLabel,timeLabel);
                        //messageTextLabel.setAlignment(Pos.CENTER_RIGHT);
                        tempMessage.setTranslateX(5);
                        timeLabel.setTranslateX(finalMessageWidth-25);
                        timeLabel.setFont(Font.font(9));
                        tempMessage.setMinWidth(finalMessageWidth);
                        tempMessage.setMaxWidth(finalMessageWidth);
                        tempMessage.setMinHeight(30);
                        messageTextLabel.setWrapText(true);
                        messageTextLabel.setTextAlignment(TextAlignment.JUSTIFY);
                        messageTextLabel.setMaxWidth(finalMessageWidth);
                    }
                    else if(messageSituation==5){
                        finalMessageWidth=max(finalMessageWidth,200);
                        Label usernameLabel=new Label(i.sender.username+" Forwarded From "+
                                DataBaseStuff.appMessages.get(i.forwardFrom-1).sender.username);
                        usernameLabel.setMaxWidth(200);
                        usernameLabel.setFont(Font.font(10));
                        tempMessage=new VBox(2,usernameLabel,messageTextLabel,timeLabel);
                        //messageTextLabel.setAlignment(Pos.CENTER_RIGHT);
                        tempMessage.setTranslateX(5);
                        timeLabel.setTranslateX(finalMessageWidth-25);
                        timeLabel.setFont(Font.font(9));
                        tempMessage.setMinWidth(finalMessageWidth);
                        tempMessage.setMaxWidth(finalMessageWidth);
                        tempMessage.setMinHeight(30);
                        messageTextLabel.setWrapText(true);
                        messageTextLabel.setTextAlignment(TextAlignment.JUSTIFY);
                        messageTextLabel.setMaxWidth(finalMessageWidth);
                    }
                    else if(messageSituation==6){
                        finalMessageWidth=max(finalFileWidth,finalMessageWidth);
                        tempMessage=new VBox(2,tempRect,messageTextLabel,timeLabel);
                        tempMessage.setTranslateX(insideChatVBWidth-finalMessageWidth-20);
                        timeLabel.setTranslateX(finalMessageWidth-25);
                        timeLabel.setFont(Font.font(9));
                        tempMessage.setMinWidth(finalMessageWidth);
                        tempMessage.setMaxWidth(finalMessageWidth);
                        tempMessage.setMinHeight(30);
                        messageTextLabel.setWrapText(true);
                        messageTextLabel.setTextAlignment(TextAlignment.JUSTIFY);
                        messageTextLabel.setMaxWidth(finalMessageWidth);
                    }
                    else if(messageSituation==7){
                        finalMessageWidth=max(finalFileWidth,finalMessageWidth);
                        Label usernameLabel=new Label("Replying to "+DataBaseStuff.appMessages.get(i.replyTo-1).sender.username+": "+
                                DataBaseStuff.appMessages.get(i.replyTo-1).text);
                        usernameLabel.setMaxWidth(200);
                        usernameLabel.setFont(Font.font(10));
                        tempMessage=new VBox(2,usernameLabel,tempRect,messageTextLabel,timeLabel);
                        //messageTextLabel.setAlignment(Pos.CENTER_RIGHT);
                        tempMessage.setTranslateX(insideChatVBWidth-finalMessageWidth-20);
                        timeLabel.setTranslateX(finalMessageWidth-25);
                        timeLabel.setFont(Font.font(9));
                        tempMessage.setMinWidth(finalMessageWidth);
                        tempMessage.setMaxWidth(finalMessageWidth);
                        tempMessage.setMinHeight(30);
                        messageTextLabel.setWrapText(true);
                        messageTextLabel.setTextAlignment(TextAlignment.JUSTIFY);
                        messageTextLabel.setMaxWidth(finalMessageWidth);
                    }
                    else if(messageSituation==8){
                        finalMessageWidth=max(finalFileWidth,finalMessageWidth);
                        Label usernameLabel=new Label("Forwarded From "+DataBaseStuff.appMessages.get(i.forwardFrom-1).sender.username);
                        usernameLabel.setMaxWidth(200);
                        usernameLabel.setFont(Font.font(10));
                        tempMessage=new VBox(2,usernameLabel,tempRect,messageTextLabel,timeLabel);
                        //messageTextLabel.setAlignment(Pos.CENTER_RIGHT);
                        tempMessage.setTranslateX(insideChatVBWidth-finalMessageWidth-20);
                        timeLabel.setTranslateX(finalMessageWidth-25);
                        timeLabel.setFont(Font.font(9));
                        tempMessage.setMinWidth(finalMessageWidth);
                        tempMessage.setMaxWidth(finalMessageWidth);
                        tempMessage.setMinHeight(30);
                        messageTextLabel.setWrapText(true);
                        messageTextLabel.setTextAlignment(TextAlignment.JUSTIFY);
                        messageTextLabel.setMaxWidth(finalMessageWidth);
                    }
                    else if(messageSituation==9){
                        finalMessageWidth=max(finalFileWidth,finalMessageWidth);
                        Label usernameLabel=new Label(i.sender.username);
                        usernameLabel.setMaxWidth(200);
                        usernameLabel.setFont(Font.font(10));
                        tempMessage=new VBox(2,usernameLabel,tempRect,messageTextLabel,timeLabel);
                        //messageTextLabel.setAlignment(Pos.CENTER_RIGHT);
                        tempMessage.setTranslateX(5);
                        timeLabel.setTranslateX(finalMessageWidth-25);
                        timeLabel.setFont(Font.font(9));
                        tempMessage.setMinWidth(finalMessageWidth);
                        tempMessage.setMaxWidth(finalMessageWidth);
                        tempMessage.setMinHeight(30);
                        messageTextLabel.setWrapText(true);
                        messageTextLabel.setTextAlignment(TextAlignment.JUSTIFY);
                        messageTextLabel.setMaxWidth(finalMessageWidth);
                    }
                    else if(messageSituation==10){
                        finalMessageWidth=max(finalFileWidth,finalMessageWidth);
                        Label usernameLabel=new Label(i.sender.username+" Replying to "+DataBaseStuff.appMessages.get(i.replyTo-1).
                                sender.username+": "+ DataBaseStuff.appMessages.get(i.replyTo-1).text);
                        usernameLabel.setMaxWidth(200);
                        usernameLabel.setFont(Font.font(10));
                        tempMessage=new VBox(2,usernameLabel,tempRect,messageTextLabel,timeLabel);
                        //messageTextLabel.setAlignment(Pos.CENTER_RIGHT);
                        tempMessage.setTranslateX(5);
                        timeLabel.setTranslateX(finalMessageWidth-25);
                        timeLabel.setFont(Font.font(9));
                        tempMessage.setMinWidth(finalMessageWidth);
                        tempMessage.setMaxWidth(finalMessageWidth);
                        tempMessage.setMinHeight(30);
                        messageTextLabel.setWrapText(true);
                        messageTextLabel.setTextAlignment(TextAlignment.JUSTIFY);
                        messageTextLabel.setMaxWidth(finalMessageWidth);
                    }
                    else if(messageSituation==11){
                        finalMessageWidth=max(finalFileWidth,finalMessageWidth);
                        Label usernameLabel=new Label(i.sender.username+" Forwarded From "+DataBaseStuff.
                                appMessages.get(i.forwardFrom-1).sender.username);
                        usernameLabel.setMaxWidth(200);
                        usernameLabel.setFont(Font.font(10));
                        tempMessage=new VBox(2,usernameLabel,tempRect,messageTextLabel,timeLabel);
                        //messageTextLabel.setAlignment(Pos.CENTER_RIGHT);
                        tempMessage.setTranslateX(5);
                        timeLabel.setTranslateX(finalMessageWidth-25);
                        timeLabel.setFont(Font.font(9));
                        tempMessage.setMinWidth(finalMessageWidth);
                        tempMessage.setMaxWidth(finalMessageWidth);
                        tempMessage.setMinHeight(30);
                        messageTextLabel.setWrapText(true);
                        messageTextLabel.setTextAlignment(TextAlignment.JUSTIFY);
                        messageTextLabel.setMaxWidth(finalMessageWidth);
                    }
                    String cssLayout = "-fx-border-color: black;\n" +
                            "-fx-border-insets: 1;\n" +
                            "-fx-border-width: 1;\n";
                    tempMessage.setStyle(cssLayout);
                    messagesVBs.add(tempMessage);
                    messagesVB.getChildren().add(messagesVBs.get(a));
                    a++;
                }
            }
            ScrP.setContent(messagesVB);
            ScrP.setVvalue(1);
            for(int i=0;i<messagesVBs.size();i++){
                int finalI = i;
                messagesVBs.get(i).addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                    long startTime;
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                            startTime = System.currentTimeMillis();
                        } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                            if (500<System.currentTimeMillis()-startTime) {
                                Stage messageOptionsStage=new Stage();
                                RadioButton o1=new RadioButton("Edit"),o2=new RadioButton("Reply"),
                                        o3=new RadioButton("Forward");
                                ToggleGroup options=new ToggleGroup();
                                o1.setToggleGroup(options);
                                o2.setToggleGroup(options);
                                o3.setToggleGroup(options);
                                VBox optionVB=new VBox(5,o1,o2,o3);
                                options.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
                                    public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n)
                                    {
                                        RadioButton rb = (RadioButton)options.getSelectedToggle();
                                        if (rb != null) {
                                            if(rb.getText().equals("Edit")){
                                                if(!currMessages.get(finalI).sender.equals(currUser) ||
                                                        currMessages.get(finalI).forwardFrom!=0){
                                                    Stage tempAlert=Alerts.Alert("Error!",
                                                            "You Can Only Edit Messages You Wrote!");
                                                    tempAlert.show();
                                                }
                                                else if(currMessages.get(finalI).hasBeenForwarded){
                                                    Stage tempAlert=Alerts.Alert("Error!","You Can Not Edit Messages" +
                                                            " That Have Been Forwarded!");
                                                    tempAlert.show();
                                                }
                                                else{
                                                    boolean hasFile=false;
                                                    if(currMessages.get(finalI).filePath.length()!=0){
                                                        hasFile=true;
                                                    }
                                                    editMessage(primaryStage,currUser,hasFile,currMessages.get(finalI),currChat);
                                                }
                                            }
                                            else if(rb.getText().equals("Reply")){
                                                if(replyTo[0]==currMessages.get(finalI).ID){
                                                    Stage tempAlert=Alerts.Alert("Done!","Replying Canceled!");
                                                    tempAlert.show();
                                                    replyTo[0]=0;
                                                }
                                                else{
                                                    replyTo[0]=currMessages.get(finalI).ID;
                                                    Stage tempAlert=Alerts.Alert("Done!","Selected Message to be Replied");
                                                    tempAlert.show();
                                                }
                                            }
                                            else if(rb.getText().equals("Forward")){
                                                if(forwardFrom[0]==currMessages.get(finalI).ID){
                                                    Stage tempAlert=Alerts.Alert("Done!","Forwarding Canceled!");
                                                    tempAlert.show();
                                                    forwardFrom[0]=0;
                                                }
                                                else {
                                                    forwardFrom[0]=currMessages.get(finalI).ID;
                                                    try {
                                                        updateCurrUserNeutralMessagesVB(primaryStage,currUser,new int[]{0},forwardFrom);
                                                        showInsideChat(primaryStage);
                                                    } catch (FileNotFoundException e) {
                                                        e.printStackTrace();
                                                    }
                                                    Stage tempAlert=Alerts.Alert("Done!", "Selected Message to be Forwarded");
                                                    tempAlert.show();
                                                }
                                            }
                                            messageOptionsStage.close();
                                        }
                                    }
                                });
                                Scene optionsScene=new Scene(optionVB);
                                int col=50;
                                if(Theme.isDark){
                                    optionsScene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                                    optionsScene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
                                }
                                messageOptionsStage.setScene(optionsScene);
                                messageOptionsStage.show();
                            }
                        }
                    }
                });
            }
        }
        TextField messageTF=new TextField();
        messageTF.setPromptText("Enter Your Message Here!");
        messageTF.setMinSize(insideChatVBWidth-130,30);
        messageTF.setMaxSize(insideChatVBWidth-130,30);
        Button submitButton=new Button("Send"),chooseFileButton=new Button("Choose File");
        submitButton.setMinSize(50,30);
        submitButton.setMaxSize(50,30);
        chooseFileButton.setMinSize(80,30);
        chooseFileButton.setMaxSize(80,30);
        if(flag==0){
            if(selectedUser.blockedUsers.contains(currUser) || currUser.blockedUsers.contains(selectedUser)){
                submitButton.setDisable(true);
            }
        }
        FileChooser file_chooser = new FileChooser();
        String[] filePath={""};
        File[] file = new File[1];
        EventHandler<ActionEvent> event1=new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                file[0]=file_chooser.showOpenDialog(new Stage());
                if (file[0] != null) {
                    filePath[0]=file[0].getAbsolutePath();
                    String mimetype= new MimetypesFileTypeMap().
                            getContentType(file[0]);
                    String type = mimetype.split("/")[0];
                    Stage alert;
                    if(type.equals("image")){
                        alert=Alerts.Alert("Operation Was Successful!","File Selected!");
                    }
                    else{
                        alert=Alerts.Alert("Operation Failed!","Select An Image!");
                    }
                    alert.show();
                }
            }
        };
        chooseFileButton.setOnAction(event1);
        int finalFlag=flag;
        AppUser finalSelectedUser=selectedUser;
        messageTF.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent k) {
                if (k.getCode().equals(KeyCode.ENTER)) {
                    boolean sendMessage=true;
                    if(finalFlag==0){
                        if(finalSelectedUser.blockedUsers.contains(currUser) || currUser.blockedUsers.contains(finalSelectedUser)){
                            sendMessage=false;
                        }
                    }
                    if(sendMessage){
                        sendChatMessage(primaryStage,currUser,messageTF.getText(),filePath[0],0, replyTo[0],currChat,finalFlag);
                        filePath[0]="";
                        try {
                            updateCurrUserNeutralMessagesVB(primaryStage,currUser,new int[]{0},new int[]{0});
                            updateInsideChatVB(primaryStage,currUser,currChat);
                            showInsideChat(primaryStage);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        submitButton.setOnAction(actionEvent -> {
            sendChatMessage(primaryStage,currUser,messageTF.getText(),filePath[0],0,replyTo[0],currChat,finalFlag);
            filePath[0]="";
            try {
                updateCurrUserNeutralMessagesVB(primaryStage,currUser,new int[]{0},new int[]{0});
                updateInsideChatVB(primaryStage,currUser,currChat);
                showInsideChat(primaryStage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        int finalFlag1 = flag;
        AppGroup finalSelectedGroup=selectedGroup;
        AppGroup finalSelectedGroup1 = selectedGroup;
        AppGroup finalSelectedGroup2 = selectedGroup;
        finalHeaderHB.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            long startTime;
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                    startTime = System.currentTimeMillis();
                } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                    if (500 < System.currentTimeMillis() - startTime) {
                        if (finalFlag1 == 2) {
                            Stage editGroupStage=new Stage();
                            InputStream stream1=null;
                            try {
                                stream1=new FileInputStream(currChat.filePath);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            Image image1 = new Image(stream1);
                            Circle circle= new Circle(20);
                            ImagePattern imagePattern = new ImagePattern(image1);
                            circle.setFill(imagePattern);
                            circle.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                                long startTime;
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                                        startTime = System.currentTimeMillis();
                                    } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                                        if(500<System.currentTimeMillis()-startTime) {
                                            try {
                                                Stage stage3=new Stage();
                                                stage3.setTitle("Set A Group Picture");
                                                FileChooser file_chooser = new FileChooser();
                                                Label label = new Label("No Picture Selected");
                                                Button button = new Button("Find a Picture in Your Computer");
                                                File[] file = new File[1];
                                                EventHandler<ActionEvent> event1=new EventHandler<ActionEvent>() {
                                                    public void handle(ActionEvent e)
                                                    {
                                                        file[0]=file_chooser.showOpenDialog(stage3);
                                                        if (file[0] != null) {
                                                            String filepath=file[0].getAbsolutePath();
                                                            String mimetype= new MimetypesFileTypeMap().
                                                                    getContentType(file[0]);
                                                            String type = mimetype.split("/")[0];
                                                            if(type.equals("image")){
                                                                finalSelectedGroup.filePath=filepath;
                                                                currChat.filePath=filepath;
                                                                try {
                                                                    updateCurrUserNeutralMessagesVB(primaryStage,currUser,
                                                                            new int[]{0},new int[]{0});
                                                                    updateInsideChatVB(primaryStage,currUser,currChat);
                                                                    showInsideChat(primaryStage);
                                                                } catch (FileNotFoundException e6) {
                                                                    e6.printStackTrace();
                                                                }
                                                                stage3.close();
                                                                editGroupStage.close();
                                                            }
                                                            else{
                                                                label.setText("Try Selecting An Image");
                                                            }
                                                        }
                                                    }
                                                };
                                                button.setOnAction(event1);
                                                VBox vbox = new VBox(30, label, button);
                                                vbox.setAlignment(Pos.CENTER);
                                                Scene newProfScene=new Scene(vbox, 300,100);
                                                int col=40;
                                                if(Theme.isDark){
                                                    newProfScene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                                                    newProfScene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
                                                }
                                                stage3.setScene(newProfScene);
                                                stage3.show();
                                            }
                                            catch (Exception e) {
                                                System.out.println(e.getMessage());
                                            }
                                        }
                                    }
                                }
                            });
                            Label titleLabel = new Label("Group Members");
                            ListView<String> listView = new ListView<>();
                            ObservableList<String> list = FXCollections.observableArrayList();
                            listView.setItems(list);
                            Label nameIDLabel = new Label("Enter Group Name & ID"), bioLabel = new Label("Enter Group Description");
                            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                            listView.setMaxHeight(200);
                            listView.setOnMouseClicked(new EventHandler<Event>() {
                                @Override
                                public void handle(Event event) {
                                    String s=listView.getSelectionModel().getSelectedItem();
                                    if(currUser.equals(finalSelectedGroup1.admin)){
                                        for(AppUser i:finalSelectedGroup1.members){
                                            if(i.username.equals(s)){
                                                if(!i.equals(finalSelectedGroup1.admin)){
                                                    Stage banRemoveStage=new Stage();
                                                    RadioButton o1=new RadioButton("Remove This User"),
                                                            o2=new RadioButton("Ban This User");
                                                    VBox banRemoveVB=new VBox(5,o1,o2);
                                                    ToggleGroup banRemove=new ToggleGroup();
                                                    o1.setToggleGroup(banRemove);
                                                    o2.setToggleGroup(banRemove);
                                                    banRemove.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
                                                        public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n){
                                                            RadioButton rb=(RadioButton)banRemove.getSelectedToggle();
                                                            if(rb!=null){
                                                                if(rb.getText().equals("Remove This User")){
                                                                    i.chats.remove(currChat);
                                                                    i.pinnedChats.remove(currChat);
                                                                    finalSelectedGroup1.members.remove(i);
                                                                }
                                                                else if(rb.getText().equals("Ban This User")){
                                                                    i.chats.remove(currChat);
                                                                    i.pinnedChats.remove(currChat);
                                                                    finalSelectedGroup1.members.remove(i);
                                                                    finalSelectedGroup1.bannedUsers.add(i);
                                                                }
                                                                banRemoveStage.close();
                                                                editGroupStage.close();
                                                                try {
                                                                    updateInsideChatVB(primaryStage,currUser,currChat);
                                                                    showInsideChat(primaryStage);
                                                                } catch (FileNotFoundException e6) {
                                                                    e6.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    });
                                                    Scene banRemoveScene=new Scene(banRemoveVB);
                                                    banRemoveStage.setScene(banRemoveScene);
                                                    banRemoveStage.show();
                                                }
                                                break;
                                            }
                                        }

                                    }
                                }
                            });
                            TextField tfName = new TextField(finalSelectedGroup.name),
                                    tfID = new TextField(finalSelectedGroup.groupName), tfBio = new TextField(finalSelectedGroup.bio);
                            tfName.setPromptText("Enter The Group's Name Here!");
                            tfID.setPromptText("Enter The Group's ID Here!");
                            tfBio.setPromptText("Enter The Group's Description Here!");
                            Button b = new Button("submit");
                            for (AppUser i : finalSelectedGroup.members) {
                                list.add(i.username);
                            }
                            b.setOnAction(e -> {
                                if (tfName.getText().length() == 0 || tfID.getText().length() == 0) {
                                    nameIDLabel.setText("Group Has to Have Name & ID");
                                } else {
                                    boolean flag = true;
                                    for (AppGroup i : DataBaseStuff.appGroups) {
                                        if (tfID.getText().equals(i.groupName)) {
                                            if(!i.equals(finalSelectedGroup)){
                                                nameIDLabel.setText("Group With This ID Already Exists");
                                                flag = false;
                                            }
                                            break;
                                        }
                                    }
                                    if (flag) {
                                        finalSelectedGroup.name=tfName.getText();
                                        finalSelectedGroup.groupName=tfID.getText();
                                        finalSelectedGroup.bio=tfBio.getText();
                                        currChat.name=tfName.getText();
                                        editGroupStage.close();
                                        try {
                                            updateCurrUserNeutralMessagesVB(primaryStage,currUser,
                                                    new int[]{0},new int[]{0});
                                            updateInsideChatVB(primaryStage,currUser,currChat);
                                            showInsideChat(primaryStage);
                                        } catch (FileNotFoundException e6) {
                                            e6.printStackTrace();
                                        }
                                    }
                                }
                            });
                            Button addUser=new Button("Add User");
                            addUser.setOnAction(e -> {
                                Stage addUserStage=new Stage();
                                ListView<String> listView1=new ListView<>();
                                ObservableList<String> list1=FXCollections.observableArrayList();
                                listView1.setItems(list1);
                                Label addUserLabel=new Label("Add User");
                                listView1.setMaxHeight(200);
                                listView1.setOnMouseClicked(new EventHandler<Event>() {
                                    @Override
                                    public void handle(Event event) {
                                        String s=listView1.getSelectionModel().getSelectedItem();
                                        for(AppUser i:currUser.followers){
                                            if(i.username.equals(s)){
                                                finalSelectedGroup.bannedUsers.remove(i);
                                                finalSelectedGroup.members.add(i);
                                                i.chats.add(currChat);
                                                editGroupStage.close();
                                                addUserStage.close();
                                                try {
                                                    updateInsideChatVB(primaryStage,currUser,currChat);
                                                    showInsideChat(primaryStage);
                                                } catch (FileNotFoundException e6) {
                                                    e6.printStackTrace();
                                                }
                                                break;
                                            }
                                        }
                                    }
                                });
                                for(AppUser i:currUser.followers){
                                    if(currUser.equals(finalSelectedGroup.admin)){
                                        if(!finalSelectedGroup2.members.contains(i)){
                                            list1.add(i.username);
                                        }
                                    }
                                    else{
                                        if(!(finalSelectedGroup2.members.contains(i) || finalSelectedGroup2.bannedUsers.contains(i))){
                                            list1.add(i.username);
                                        }
                                    }
                                }
                                VBox addUserVB=new VBox(5,addUserLabel,listView1);
                                Scene addUserScene=new Scene(addUserVB);
                                int col=50;
                                if(Theme.isDark){
                                    addUserScene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                                    addUserScene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
                                }
                                addUserStage.setScene(addUserScene);
                                addUserStage.show();
                            });
                            int newGroupVBWidth=300;
                            HBox namesHB=new HBox(tfName,tfID);
                            tfName.setMinWidth(newGroupVBWidth/2);
                            tfName.setMaxWidth(newGroupVBWidth/2);
                            tfID.setMinWidth(newGroupVBWidth/2);
                            tfID.setMaxWidth(newGroupVBWidth/2);
                            VBox fin=new VBox(5,circle,nameIDLabel,namesHB,bioLabel,tfBio,titleLabel,listView,addUser,b);
                            Scene scene=new Scene(fin);
                            fin.setAlignment(Pos.CENTER);
                            fin.setMinWidth(newGroupVBWidth);
                            fin.setMaxWidth(newGroupVBWidth);
                            int col=50;
                            if(Theme.isDark){
                                scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                                scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
                            }
                            editGroupStage.setScene(scene);
                            editGroupStage.show();
                        }
                    }
                }
            }
        });

        VBox finalVB=new VBox(finalHeaderHB,ScrP,new HBox(messageTF,chooseFileButton,submitButton));
        currUserInsideChatVB=finalVB;
    }
    private static void editMessage(Stage primaryStage, AppUser currUser, boolean hasFile, AppMessage appMessage,AppChat currChat) {
        try {
            Stage stage=new Stage();
            stage.setTitle("Edit Message");
            FileChooser file_chooser = new FileChooser();
            TextField tfCaption=new TextField(appMessage.text);
            tfCaption.setPromptText("Your Text Goes Here!");
            String[] filePath={""};
            Label label=new Label("No Picture Selected");
            if(hasFile){
                label.setText("This Post Already Has An Image, But You Can Change It");
                filePath[0]=appMessage.filePath;
            }
            Button button = new Button("Find a Picture in Your Computer"),
                    b4=new Button("remove Image From This Message"),
                    button1=new Button("Edit This Message"),b3=new Button("Delete This Message Completely"),
            b5=new Button("Delete This Message For Yourself");
            b3.setOnAction(ActionEvent-> {
                appMessage.isDeleted=true;
                try {
                    updateCurrUserNeutralMessagesVB(primaryStage,currUser,new int[]{0},new int[]{0});
                    updateInsideChatVB(primaryStage,currUser,currChat);
                    showInsideChat(primaryStage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                stage.close();
            });
            b5.setOnAction(ActionEvent-> {
                appMessage.isDeletedFor.add(currUser);
                try {
                    updateCurrUserNeutralMessagesVB(primaryStage,currUser,new int[]{0},new int[]{0});
                    updateInsideChatVB(primaryStage,currUser,currChat);
                    showInsideChat(primaryStage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                stage.close();
            });
            b4.setOnAction(ActionEvent-> {
                appMessage.filePath="";
                try {
                    updateCurrUserNeutralMessagesVB(primaryStage,currUser,new int[]{0},new int[]{0});
                    updateInsideChatVB(primaryStage,currUser,currChat);
                    showInsideChat(primaryStage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                stage.close();
            });
            button1.setOnAction(ActionEvent-> {
                if(tfCaption.getText().length()==0){
                    label.setText("Post Has to Have Caption!");
                }
                else{
                    appMessage.filePath=filePath[0];
                    appMessage.text=tfCaption.getText();
                    try {
                        updateCurrUserNeutralMessagesVB(primaryStage,currUser,new int[]{0},new int[]{0});
                        updateInsideChatVB(primaryStage,currUser,currChat);
                        showInsideChat(primaryStage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    stage.close();
                }
            });
            File[] file = new File[1];
            EventHandler<ActionEvent> event1=new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e)
                {
                    file[0]=file_chooser.showOpenDialog(stage);
                    if (file[0] != null) {
                        filePath[0]=file[0].getAbsolutePath();
                        String mimetype= new MimetypesFileTypeMap().
                                getContentType(file[0]);
                        String type = mimetype.split("/")[0];
                        if(type.equals("image")){
                            label.setText("New Image Selected Successfully");
                        }
                        else{
                            label.setText("If You Want to Add A File, It Has to Be An Image!");
                        }
                    }
                }
            };
            button.setOnAction(event1);
            HBox buttonHB=new HBox(10,button1,b3),b1HB=new HBox(10,button,b4);
            VBox vbox = new VBox(30,label,tfCaption,b1HB,buttonHB,b5);
            vbox.setAlignment(Pos.CENTER);
            buttonHB.setAlignment(Pos.CENTER);
            b1HB.setAlignment(Pos.CENTER);
            Scene newProfScene=new Scene(vbox, 400,250);
            int col=40;
            if(Theme.isDark){
                newProfScene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                newProfScene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
            }
            stage.setScene(newProfScene);
            stage.show();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void sendChatMessage(Stage primaryStage, AppUser currUser, String text, String filePath,int forwardFrom,int replyTo,
                                       AppChat currChat, int flag){
        if(text.length()!=0){
            AppMessage newMessage=new AppMessage(currUser,currUser.ID+"_"+currChat.chatID,
                    DataBaseStuff.appMessages.size()+1,replyTo,forwardFrom,text,filePath);
            if(flag==0){
                for(AppUser i:DataBaseStuff.appUsers){
                    if(i.ID==currChat.chatID){
                        AppChat newChat=new AppChat();
                        newChat.chatID=currUser.ID;
                        newChat.name=currUser.username;
                        newChat.filePath=currUser.filePath;
                        if(i.chats.contains(newChat)){
                            for(AppChat j: i.chats){
                                if(j.equals(newChat)){
                                    newChat.messages.addAll(j.messages);
                                    newChat.messages.add(newMessage);
                                    i.chats.remove(j);
                                    i.chats.add(0,newChat);
                                    break;
                                }
                            }
                        }
                        else if(i.pinnedChats.contains(newChat)){
                            for(AppChat j:i.pinnedChats){
                                if(j.equals(newChat)){
                                    j.messages.add(newMessage);
                                    break;
                                }
                            }
                        }
                        else{
                            newChat.messages.add(newMessage);
                            i.chats.add(0,newChat);
                        }
                        break;
                    }
                }
                if(currUser.chats.contains(currChat)){
                    currChat.messages.add(newMessage);
                    currUser.chats.remove(currChat);
                    currUser.chats.add(0,currChat);

                }
                else if(currUser.pinnedChats.contains(currChat)){
                    currChat.messages.add(newMessage);
                }
                else{
                    currChat.messages.add(newMessage);
                }
            }
            else if(flag==1){
                currChat.messages.add(newMessage);
            }
            else if(flag==2){
                for(AppUser i:DataBaseStuff.appGroups.get(-currChat.chatID-1).members){
                    AppChat newChat=new AppChat();
                    newChat.chatID=currChat.chatID;
                    newChat.name=currUser.username;
                    newChat.filePath=currUser.filePath;
                    if(i.chats.contains(newChat)){
                        for(AppChat j: i.chats){
                            if(j.equals(newChat)){
                                newChat.messages.addAll(j.messages);
                                newChat.messages.add(newMessage);
                                i.chats.remove(j);
                                i.chats.add(0,newChat);
                                break;
                            }
                        }
                    }
                    else if(i.pinnedChats.contains(newChat)){
                        for(AppChat j:i.pinnedChats){
                            if(j.equals(newChat)){
                                j.messages.add(newMessage);
                                break;
                            }
                        }
                    }
                    else{
                        newChat.messages.add(newMessage);
                        i.chats.add(0,newChat);
                    }
                }
                if(currUser.chats.contains(currChat)){
                    currChat.messages.add(newMessage);
                    currUser.chats.remove(currChat);
                    currUser.chats.add(0,currChat);

                }
                else if(currUser.pinnedChats.contains(currChat)){
                    currChat.messages.add(newMessage);
                }
                else{
                    currChat.messages.add(newMessage);
                    currUser.chats.add(0,currChat);
                }
            }
            DataBaseStuff.appMessages.add(newMessage);
        }
        else{
            Stage alert=Alerts.Alert("Operation Failed!","Message Has to Have Text!");
            alert.show();
        }
    }
    public static void updateCurrUserVB(Stage primaryStage,AppUser currUser) throws FileNotFoundException {
        Label userNameLabel=new Label(currUser.username),fiLabel=new Label("Followings"),
                fwLabel=new Label("Followers"),poLabel=new Label("Posts"),
        postsLabel=new Label(String.valueOf(currUser.posts.size())),
        followerLabel=new Label(String.valueOf(currUser.followers.size())),
        followingsLabel=new Label(String.valueOf(currUser.followings.size())),
                bioLabel=new Label(currUser.bio),nameLabel=new Label(currUser.name);
        nameLabel.setFont(Font.font(15));
        Button changeCurrUserSettings=new Button("Change Your Account Settings");
        InputStream stream1=new FileInputStream(currUser.filePath);
        Image image1 = new Image(stream1);
        Circle circle= new Circle(20);
        ImagePattern imagePattern = new ImagePattern(image1);
        circle.setFill(imagePattern);
        userNameLabel.setFont(Font.font("Times new roman",15));
        circle.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            long startTime;
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                    startTime = System.currentTimeMillis();
                } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                    if(500<System.currentTimeMillis()-startTime) {
                        try {
                            Stage stage3=new Stage();
                            stage3.setTitle("Set A Profile Picture");
                            FileChooser file_chooser = new FileChooser();
                            Label label = new Label("No Picture Selected");
                            Button button = new Button("Find a Picture in Your Computer");
                            File[] file = new File[1];
                            EventHandler<ActionEvent> event1=new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent e)
                                {
                                    file[0]=file_chooser.showOpenDialog(stage3);
                                    if (file[0] != null) {
                                        String filepath=file[0].getAbsolutePath();
                                        String mimetype= new MimetypesFileTypeMap().
                                                getContentType(file[0]);
                                        String type = mimetype.split("/")[0];
                                        if(type.equals("image")){
                                            currUser.filePath=filepath;
                                            try {
                                                Inside.showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
                                            } catch (FileNotFoundException ex) {
                                                ex.printStackTrace();
                                            }
                                            stage3.close();
                                        }
                                        else{
                                            label.setText("Try Selecting An Image");
                                        }
                                    }
                                }
                            };
                            button.setOnAction(event1);
                            VBox vbox = new VBox(30, label, button);
                            vbox.setAlignment(Pos.CENTER);
                            Scene newProfScene=new Scene(vbox, 300,100);
                            int col=40;
                            if(Theme.isDark){
                                newProfScene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                                newProfScene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
                            }
                            stage3.setScene(newProfScene);
                            stage3.show();
                        }
                        catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    else {
                        makeStory(primaryStage,currUser);
                    }
                }
            }
        });
        changeCurrUserSettings.setOnAction(e -> {
            ObservableList<Object> settingsList=FXCollections.observableArrayList();
            ListView settingsLV=new ListView(settingsList);
            settingsList.addAll("Name: "+currUser.name);
            settingsList.addAll("Password: "+currUser.password);
            settingsList.addAll("Security Question: "+currUser.securityQuestion);
            settingsList.addAll("Security Hint: "+currUser.securityHint);
            settingsList.addAll("Security Answer: "+currUser.securityAnswer);
            String accType="Regular",twoStep="Off",acc="Public";
            if(currUser.isBusiness){
                accType="Business";
            }
            if(currUser.twoStep){
                twoStep="On";
            }
            if(currUser.isPrivate){
                acc="Private";
            }
            settingsList.addAll("Account Type: "+accType);
            settingsList.addAll("Two Step Verification: "+twoStep);
            settingsList.addAll("Account: "+acc);
            settingsList.addAll("Email Address: "+currUser.emailAddress);
            Pane pane=new Pane();
            Stage stage=new Stage();
            Button back=new Button("Back");
            back.setOnAction(e1 -> stage.close());
            Label settingsLabel=new Label("Settings:(Press Each One to Change)");
            StackPane backSP=new StackPane(back),settingLabelSP=new StackPane(settingsLabel);
            settingsLV.setPrefSize(300,350);
            settingsLV.setEditable(true);
            settingsLV.setOnMouseClicked((EventHandler<Event>) event1 -> {
                int selected=settingsLV.getSelectionModel().getSelectedIndex();
                if(selected==0){
                    changeCurrUsersName(primaryStage,stage,currUser);
                }
                else if(selected==1){
                    changeCurrUsersPassword(stage,currUser);
                }
                else if(selected==2){
                    changeCurrUsersSecQuestion(stage,currUser);
                }
                else if(selected==5){
                    changeCurrUsersAccType(stage,currUser);
                }
                else if(selected==6){
                    changeCurrUsersTwoStep(stage,currUser);
                }
                else if(selected==7){
                    changeCurrUsersAcc(primaryStage,stage,currUser);
                }
                else if(selected==8){
                    changeCurrUsersEmail(stage,currUser);
                }
            });
            VBox VB=new VBox(settingLabelSP,settingsLV,backSP);
            pane.getChildren().addAll(VB);
            Scene scene=new Scene(pane,300,400);
            int col=50;
            if(Theme.isDark){
                scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
            }
            stage.setScene(scene);
            stage.show();
        });
        Button changeThemeButton=new Button("Change Theme");
        changeThemeButton.setOnMouseClicked(e -> {
           currUser.isDark=!currUser.isDark;
           Theme.isDark=currUser.isDark;
            try {
                showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        Button followRequestsButton=new Button("Follow Requests");
        followRequestsButton.setOnMouseClicked(e -> {
            Stage followRequestsStage=new Stage();
            ListView<String> listView1=new ListView<>();
            ObservableList<String> list1=FXCollections.observableArrayList();
            listView1.setItems(list1);
            Label addUserLabel=new Label("Follow Requests");
            listView1.setMaxHeight(200);
            listView1.setOnMouseClicked(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    String s=listView1.getSelectionModel().getSelectedItem();
                    for(AppUser i:currUser.followRequests){
                        if(i.username.equals(s)){
                            currUser.acceptFollow(i);
                            currUser.followRequests.remove(i);
                            try {
                                showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
                            } catch (FileNotFoundException e6) {
                                e6.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            });
            for(AppUser i:currUser.followRequests){
                list1.add(i.username);
            }
            VBox addUserVB=new VBox(5,addUserLabel,listView1);
            Scene addUserScene=new Scene(addUserVB);
            int col=50;
            if(Theme.isDark){
                addUserScene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                addUserScene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
            }
            followRequestsStage.setScene(addUserScene);
            followRequestsStage.show();
        });
        userNameLabel.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Stage stage1=new Stage();
                VBox changeUsernameVB=new VBox(10);
                stage1.setTitle("Change Username");
                TextField tf1=new TextField(currUser.username);
                Label l1=new Label("Enter Your Chosen Username");
                Button button1=new Button("Close"),b2=new Button("Submit");
                StackPane sp1=new StackPane(button1),sp2=new StackPane(b2);
                HBox hb=new HBox(10,sp2,sp1);
                button1.setOnAction(ActionEvent-> stage1.close());
                b2.setOnAction(ActionEvent-> {
                    int flag=1;
                    if(tf1.getText().length()==0){
                        Stage stage2=Alerts.Alert("Error!","You Should Enter A Username!");
                        stage2.show();
                    }
                    else{
                        for(AppUser i:DataBaseStuff.appUsers){
                            if(i.username.equals(tf1.getText())){
                                flag=0;
                            }
                        }
                        if(flag==0){
                            Stage stage2=Alerts.Alert("Error!","This Username Already Exists!");
                            stage2.show();
                        }
                        else if(flag==1){
                            currUser.username=tf1.getText();
                            try {
                                Inside.showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
                                //updateCurrUserVB(primaryStage,currUser);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    stage1.close();
                });
                changeUsernameVB.getChildren().addAll(new StackPane(l1),new StackPane(tf1),new StackPane(hb));
                tf1.setPrefWidth(200);
                hb.setAlignment(Pos.CENTER);
                Scene newUsernameScene=new Scene(changeUsernameVB, 300,100);
                int col=40;
                if(Theme.isDark){
                    newUsernameScene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                    newUsernameScene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
                }
                stage1.setScene(newUsernameScene);
                stage1.show();
            }
        });
        bioLabel.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Stage stage1=new Stage();
                VBox changeUsernameVB=new VBox(10);
                stage1.setTitle("Change Bio");
                TextField tf1=new TextField(currUser.bio);
                tf1.setPromptText("Your Bio");
                Label l1=new Label("Enter Your Bio");
                Button button1=new Button("Close"),b2=new Button("Submit");
                StackPane sp1=new StackPane(button1),sp2=new StackPane(b2);
                HBox hb=new HBox(10,sp2,sp1);
                button1.setOnAction(ActionEvent-> stage1.close());
                b2.setOnAction(ActionEvent-> {
                    currUser.bio= tf1.getText();
                    stage1.close();
                    try {
                        Inside.showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
                        //updateCurrUserVB(primaryStage,currUser);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                });
                changeUsernameVB.getChildren().addAll(new StackPane(l1),new StackPane(tf1),new StackPane(hb));
                tf1.setPrefWidth(200);
                hb.setAlignment(Pos.CENTER);
                Scene newUsernameScene=new Scene(changeUsernameVB, 300,100);
                int col=40;
                if(Theme.isDark){
                    newUsernameScene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                    newUsernameScene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
                }
                stage1.setScene(newUsernameScene);
                stage1.show();
            }
        });
        StackPane sp3=new StackPane(circle),usernameSP=new StackPane(userNameLabel),
        changeSettingsSP=new StackPane(changeCurrUserSettings);
        Pane bioLabelP=new Pane(bioLabel),nameLabelP=new Pane(nameLabel);
        VBox fowVB=new VBox(10,fwLabel,followerLabel),finVB=new VBox(10,fiLabel,followingsLabel),
                poVB=new VBox(10,poLabel,postsLabel);
        HBox folHB=new HBox(20,poVB,fowVB,finVB);
        changeCurrUserSettings.setStyle("-fx-focus-color: transparent;");
        ScrollPane ScrP=new ScrollPane();
        int currUserVBMinWidth=300,currUserVBMinHeight=600;
        ScrP.setMinSize(currUserVBMinWidth,currUserVBMinHeight/2+50);
        ScrP.setMaxSize(currUserVBMinWidth,currUserVBMinHeight/2+50);
        VBox postsVB=new VBox();
        if(currUser.posts.size()!=0){
            VBox[] postsVBs=new VBox[currUser.posts.size()];
            String postHeaderPath="C:\\OOP File DataBase\\neutralProfile.jpg";
            if(!currUser.filePath.equals("")){
                postHeaderPath=currUser.filePath;
            }
            InputStream tempStream, postHeaderStream;
            Image tempImage,postHeaderImage;
            Rectangle tempRect;
            Circle postHeaderCircle;
            ImagePattern tempPattern,postHeaderPattern;
            Label postHeaderLabel,postFooterLabel;
            HBox postHeaderHB;
            for(int i=0;i<currUser.posts.size();i++){
                //i.seens.
                postHeaderStream=new FileInputStream(postHeaderPath);
                postHeaderImage=new Image(postHeaderStream);
                postHeaderCircle=new Circle(20);
                postHeaderPattern=new ImagePattern(postHeaderImage);
                postHeaderCircle.setFill(postHeaderPattern);
                postHeaderLabel=new Label(currUser.username);
                postFooterLabel=new Label(currUser.username+": "+currUser.posts.get(i).caption);
                postFooterLabel.setWrapText(true);
                postFooterLabel.setTextAlignment(TextAlignment.JUSTIFY);
                postFooterLabel.setMaxWidth(currUserVBMinWidth-15);
                postHeaderHB=new HBox(10,postHeaderCircle,postHeaderLabel);
                if(currUser.posts.get(i).filePath.length()==0){
                    postsVBs[currUser.posts.size()-1-i]=new VBox(postHeaderHB,postFooterLabel);
                }
                else{
                    tempStream=new FileInputStream(currUser.posts.get(i).filePath);
                    tempImage=new Image(tempStream);
                    tempRect= new Rectangle(currUserVBMinWidth-15,currUserVBMinHeight/2-100);
                    tempPattern=new ImagePattern(tempImage);
                    tempRect.setFill(tempPattern);
                    postsVBs[currUser.posts.size()-1-i]=new VBox(postHeaderHB,tempRect,postFooterLabel);
                }
            }
            for(int i=0;i<currUser.posts.size();i++){
                int finalI = i;
                postsVBs[i].addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                    long startTime;
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if(mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)){
                            startTime=System.currentTimeMillis();
                        }
                        else if(mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)){
                            if(500<System.currentTimeMillis()-startTime){
                                boolean hasFile=false;
                                if(currUser.posts.get(currUser.posts.size()-1-finalI).filePath.length()!=0){
                                    hasFile=true;
                                }
                                //System.out.println("906: "+currUser.posts.get(currUser.posts.size()-1-finalI).filePath);
                                changePost(primaryStage,currUser,hasFile,
                                        currUser.posts.get(currUser.posts.size()-1-finalI));
                            }
                            else{
                                try {
                                    commentsLikesVB=null;
                                    addSeen=true;
                                    showPost(currUser,currUser,
                                            currUser.posts.get(currUser.posts.size()-1-finalI),new Stage());
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                });
                postsVB.getChildren().add(new BorderPane(postsVBs[i]));
            }
            ScrP.setContent(postsVB);
        }
        Button makePostButton=new Button("Make Post");
        HBox buttonsHB1=new HBox(5,makePostButton,changeThemeButton,followRequestsButton);
        buttonsHB1.setAlignment(Pos.CENTER);
        makePostButton.setOnAction(ActionEvent-> {
            makePost(primaryStage, currUser);
        });
        VBox vBox=new VBox(10,usernameSP,sp3,folHB,nameLabelP,bioLabelP,changeSettingsSP,ScrP,buttonsHB1);
        vBox.setMinSize(currUserVBMinWidth,currUserVBMinHeight);
        folHB.setAlignment(Pos.CENTER);
        Theme.isDark=currUser.isDark;
        currUserVB=vBox;
    }
    public static void updateCurrUserFollowingsVBWithFollowings(Stage primaryStage,AppUser currUser)
            throws FileNotFoundException {
        ScrollPane ScrP = new ScrollPane(),storiesSCP=new ScrollPane();
        int currUserFollowingsVBMinWidth = 350, currUserFollowingsVBMinHeight = 625;
        ScrP.setMinSize(currUserFollowingsVBMinWidth, currUserFollowingsVBMinHeight -120);
        ScrP.setMaxSize(currUserFollowingsVBMinWidth, currUserFollowingsVBMinHeight -120);
        storiesSCP.setMinSize(currUserFollowingsVBMinWidth, 65);
        storiesSCP.setMaxSize(currUserFollowingsVBMinWidth, 65);
        VBox otherPostsVB = new VBox();
        HBox otherStoriesHB=new HBox(5);
        ArrayList<AppPost> currUserFollowingsPosts=new ArrayList<>();
        ArrayList<AppStory> currUserFollowingsStories=new ArrayList<>();
        for (AppPost i : DataBaseStuff.appPosts) {
            if ((i.sender.followers.contains(currUser)) || (i.sender.equals(currUser))) {
                if(!i.isDeleted){
                    currUserFollowingsPosts.add(i);
                }
            }
        }
        if (currUserFollowingsPosts.size() != 0) {
            VBox[] postsVBs = new VBox[currUserFollowingsPosts.size()];
            String postHeaderPath;
            InputStream tempStream, postHeaderStream;
            Image tempImage, postHeaderImage;
            Rectangle tempRect;
            Circle postHeaderCircle;
            ImagePattern tempPattern, postHeaderPattern;
            Label postHeaderLabel, postFooterLabel;
            HBox postHeaderHB;
            for (int i = 0; i < currUserFollowingsPosts.size(); i++) {
                postHeaderPath=currUserFollowingsPosts.get(i).sender.filePath;
                postHeaderStream = new FileInputStream(postHeaderPath);
                postHeaderImage = new Image(postHeaderStream);
                postHeaderCircle = new Circle(20);
                postHeaderPattern = new ImagePattern(postHeaderImage);
                postHeaderCircle.setFill(postHeaderPattern);
                postHeaderLabel = new Label(currUserFollowingsPosts.get(i).sender.username);
                postFooterLabel = new Label(currUserFollowingsPosts.get(i).sender.username + ": " +
                        currUserFollowingsPosts.get(i).caption);
                postFooterLabel.setWrapText(true);
                postFooterLabel.setTextAlignment(TextAlignment.JUSTIFY);
                postFooterLabel.setMaxWidth(currUserFollowingsVBMinWidth-15);
                postHeaderHB = new HBox(10, postHeaderCircle, postHeaderLabel);
                if (currUserFollowingsPosts.get(i).filePath.length() == 0) {
                    postsVBs[currUserFollowingsPosts.size() - 1 - i] = new VBox(postHeaderHB, postFooterLabel);
                } else {
                    tempStream = new FileInputStream(currUserFollowingsPosts.get(i).filePath);
                    tempImage = new Image(tempStream);
                    tempRect = new Rectangle(currUserFollowingsVBMinWidth - 15,
                            currUserFollowingsVBMinHeight / 2 - 100);
                    tempPattern = new ImagePattern(tempImage);
                    tempRect.setFill(tempPattern);
                    postsVBs[currUserFollowingsPosts.size() - 1 - i] = new VBox(postHeaderHB, tempRect
                            , postFooterLabel);
                }
            }
            for (int i = 0; i < currUserFollowingsPosts.size(); i++) {
                int finalI = i;
                postsVBs[i].addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                    long startTime;
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                            startTime = System.currentTimeMillis();
                        } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                            if (500 < System.currentTimeMillis() - startTime) {
                                if(currUserFollowingsPosts.get(currUserFollowingsPosts.size()-1-finalI).filePath.length()!=0){
                                    Stage tempStage=Alerts.Alert("Post's Image Downloaded",
                                            "Download Was Successful");
                                    tempStage.show();
                                }
                                saveFile(currUserFollowingsPosts.get(currUserFollowingsPosts.size()-1-finalI).filePath,"Post",
                                        String.valueOf(currUserFollowingsPosts.get(currUserFollowingsPosts.size()-1-finalI).sender.ID));
                            } else {
                                try {
                                    commentsLikesVB=null;
                                    addSeen=true;
                                    showPost(currUser,currUserFollowingsPosts.get(currUserFollowingsPosts.size()-1-finalI).sender,
                                            currUserFollowingsPosts.get(currUserFollowingsPosts.size()-1-finalI),new Stage());
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                otherPostsVB.getChildren().add(new BorderPane(postsVBs[i]));
            }
        }
        else{
            HBox noPostLabelHB=new HBox(new StackPane(new Label("No Posts to Show Yet!")));
            otherPostsVB.getChildren().add(noPostLabelHB);
            noPostLabelHB.setAlignment(Pos.CENTER);
        }
        ScrP.setContent(otherPostsVB);
        Button searchUsersButton=new Button("Search Users"),strangersPostsButton=new Button("Show Strangers' Posts");
        Button logOutButton=new Button("Log Out");
        logOutButton.setOnMouseClicked(e -> {
            try {
                logOut(currUser);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            primaryStage.close();
        });
        HBox buttonsHB=new HBox(20,searchUsersButton,strangersPostsButton,logOutButton);
        strangersPostsButton.setOnAction(e-> {
            strangers=true;
            try {
                showNeutralCurrUserPage(primaryStage, currUser,new int[]{0});
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        searchUsersButton.setOnAction(e-> {
            currentUser=currUser;
            //primaryStage.close();
            actualPrimaryStage=primaryStage;
            SearchUser searchUser=new SearchUser();
            Stage searchStage=new Stage();
            searchUser.start(searchStage);
            searchStage.setTitle("Search Users");

        });
        buttonsHB.setAlignment(Pos.CENTER);
        VBox finalVB=new VBox(5,storiesSCP,ScrP,buttonsHB);
        for (AppStory i:DataBaseStuff.appStories) {
            if ((i.sender.followers.contains(currUser)) || (i.sender.equals(currUser))) {
                if(!i.isDeleted && !AppStory.delete(i,false)){
                    currUserFollowingsStories.add(i);
                }
            }
        }
        if(currUserFollowingsStories.size()!= 0) {
            VBox[] storiesVBs = new VBox[currUserFollowingsStories.size()];
            String storyHeaderPath;
            InputStream storyHeaderStream;
            Image storyHeaderImage;
            Circle storyHeaderCircle;
            ImagePattern tempPattern, storyHeaderPattern;
            Label storyLabel;
            VBox storyHeaderVB;
            for(int i=0;i<currUserFollowingsStories.size();i++) {
                storyHeaderPath=currUserFollowingsStories.get(i).sender.filePath;
                storyHeaderStream=new FileInputStream(storyHeaderPath);
                storyHeaderImage=new Image(storyHeaderStream);
                storyHeaderCircle=new Circle(15);
                storyHeaderPattern=new ImagePattern(storyHeaderImage);
                storyHeaderCircle.setFill(storyHeaderPattern);
                storyLabel=new Label(currUserFollowingsStories.get(i).sender.username);
                storyHeaderVB=new VBox(2, storyHeaderCircle,storyLabel);
                storiesVBs[i]=storyHeaderVB;
            }
            for (int i = 0; i < currUserFollowingsStories.size(); i++) {
                int finalI=i;
                storiesVBs[i].addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                    long startTime;
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                            startTime = System.currentTimeMillis();
                        } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                            if (500<System.currentTimeMillis()-startTime) {
                                if(currUserFollowingsStories.get(finalI).filePath.length()!=0){
                                    Stage tempStage=Alerts.Alert("Story's Image Downloaded",
                                            "Download Was Successful");
                                    tempStage.show();
                                }
                                saveFile(currUserFollowingsStories.get(finalI).filePath,"Story",
                                        String.valueOf(currUserFollowingsStories.get(finalI).sender.ID));
                            } else {
                                Stage storyStage=new Stage();
                                try {
                                    showStories(finalI,currUserFollowingsStories,currUser,storyStage,primaryStage);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                otherStoriesHB.getChildren().add(new BorderPane(storiesVBs[i]));
            }
        }
        else{
            HBox noStoryLabelHB=new HBox(new StackPane(new Label("No Stories to Show Yet!")));
            otherStoriesHB.getChildren().add(noStoryLabelHB);
            otherStoriesHB.setAlignment(Pos.CENTER);
        }
        storiesSCP.setContent(otherStoriesHB);
        Theme.isDark=currUser.isDark;
        currUserFollowingsVB=finalVB;
    }
    public static void updateCurrUserFollowingsVBWithStrangers(Stage primaryStage,AppUser currUser)
            throws FileNotFoundException {
        ScrollPane ScrP = new ScrollPane(),storiesSCP=new ScrollPane();
        int currUserFollowingsVBMinWidth = 350, currUserFollowingsVBMinHeight = 625;
        ScrP.setMinSize(currUserFollowingsVBMinWidth, currUserFollowingsVBMinHeight - 120);
        ScrP.setMaxSize(currUserFollowingsVBMinWidth, currUserFollowingsVBMinHeight - 120);
        storiesSCP.setMinSize(currUserFollowingsVBMinWidth, 65);
        storiesSCP.setMaxSize(currUserFollowingsVBMinWidth, 65);
        VBox otherPostsVB = new VBox();
        HBox otherStoriesHB=new HBox(5);
        ArrayList<AppPost> currUserStrangersPosts = new ArrayList<>();
        for (AppPost i : DataBaseStuff.appPosts) {
            if (!((i.sender.followers.contains(currUser)) || (i.sender.equals(currUser)))) {
                if(!i.sender.isPrivate){
                    if(!i.isDeleted){
                        currUserStrangersPosts.add(i);
                    }
                }
            }
        }
        if (currUserStrangersPosts.size()!=0) {
            VBox[] postsVBs = new VBox[currUserStrangersPosts.size()];
            String postHeaderPath;
            InputStream tempStream, postHeaderStream;
            Image tempImage, postHeaderImage;
            Rectangle tempRect;
            Circle postHeaderCircle;
            ImagePattern tempPattern, postHeaderPattern;
            Label postHeaderLabel, postFooterLabel;
            HBox postHeaderHB;
            for (int i = 0; i <currUserStrangersPosts.size(); i++) {
                postHeaderPath=currUserStrangersPosts.get(i).sender.filePath;
                postHeaderStream = new FileInputStream(postHeaderPath);
                postHeaderImage = new Image(postHeaderStream);
                postHeaderCircle = new Circle(20);
                postHeaderPattern = new ImagePattern(postHeaderImage);
                postHeaderCircle.setFill(postHeaderPattern);
                postHeaderLabel = new Label(currUserStrangersPosts.get(i).sender.username);
                postFooterLabel = new Label(currUserStrangersPosts.get(i).sender.username + ": " +
                        currUserStrangersPosts.get(i).caption);
                postFooterLabel.setWrapText(true);
                postFooterLabel.setTextAlignment(TextAlignment.JUSTIFY);
                postFooterLabel.setMaxWidth(currUserFollowingsVBMinWidth-15);
                postHeaderHB = new HBox(10, postHeaderCircle, postHeaderLabel);
                if (currUserStrangersPosts.get(i).filePath.length() == 0) {
                    postsVBs[currUserStrangersPosts.size() - 1 - i] = new VBox(postHeaderHB, postFooterLabel);
                } else {
                    tempStream = new FileInputStream(currUserStrangersPosts.get(i).filePath);
                    tempImage = new Image(tempStream);
                    tempRect = new Rectangle(currUserFollowingsVBMinWidth - 15,
                            currUserFollowingsVBMinHeight / 2 - 100);
                    tempPattern = new ImagePattern(tempImage);
                    tempRect.setFill(tempPattern);
                    postsVBs[currUserStrangersPosts.size() - 1 - i] = new VBox(postHeaderHB, tempRect
                            , postFooterLabel);
                }
            }
            for (int i = 0; i<currUserStrangersPosts.size(); i++) {
                int finalI = i;
                postsVBs[i].addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                    long startTime;
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                            startTime = System.currentTimeMillis();
                        } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                            if (500 < System.currentTimeMillis() - startTime) {
                                if(currUserStrangersPosts.get(currUserStrangersPosts.size()-1-finalI).filePath.length()!=0){
                                    Stage tempStage=Alerts.Alert("Post's Image Downloaded",
                                            "Download Was Successful");
                                    tempStage.show();
                                }
                                saveFile(currUserStrangersPosts.get(currUserStrangersPosts.size()-1-finalI).filePath,"Post",
                                        String.valueOf(currUserStrangersPosts.get(currUserStrangersPosts.size()-1-finalI).sender.ID));
                            } else {
                                try {
                                    commentsLikesVB=null;
                                    addSeen=true;
                                    showPost(currUser,currUserStrangersPosts.get(currUserStrangersPosts.size()-1-finalI).sender,
                                            currUserStrangersPosts.get(currUserStrangersPosts.size()-1-finalI),new Stage());
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                otherPostsVB.getChildren().add(new BorderPane(postsVBs[i]));
            }
        }
        else{
            otherPostsVB.getChildren().add(new StackPane(new Label("No Posts to Show Yet!")));
        }
        ScrP.setContent(otherPostsVB);
        Button searchUsersButton=new Button("Search Users"),followingsPostsButton=new Button("Show Followings' Posts");
        Button logOutButton=new Button("Log Out");
        logOutButton.setOnMouseClicked(e -> {
            try {
                logOut(currUser);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            primaryStage.close();
        });
        HBox buttonsHB=new HBox(20,searchUsersButton,followingsPostsButton,logOutButton);
        followingsPostsButton.setOnAction(e-> {
            strangers=false;
            try {
                showNeutralCurrUserPage(primaryStage, currUser,new int[]{0});
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        searchUsersButton.setOnAction(e-> {
            currentUser=currUser;
            actualPrimaryStage=primaryStage;
            SearchUser searchUser=new SearchUser();
            Stage searchStage=new Stage();
            searchUser.start(searchStage);
            searchStage.setTitle("Search Users");
        });
        buttonsHB.setAlignment(Pos.CENTER);
        VBox finalVB=new VBox(5,storiesSCP,ScrP,buttonsHB);


        ArrayList<AppStory> currUserStrangersStories=new ArrayList<>();
        for (AppStory i:DataBaseStuff.appStories) {
            if (!((i.sender.followers.contains(currUser)) || (i.sender.equals(currUser)))) {
                if(!i.sender.isPrivate){
                    if(!i.isDeleted && !AppStory.delete(i,false)){
                        currUserStrangersStories.add(i);
                    }
                }
            }
        }
        if(currUserStrangersStories.size()!= 0) {
            VBox[] storiesVBs = new VBox[currUserStrangersStories.size()];
            String storyHeaderPath;
            InputStream storyHeaderStream;
            Image storyHeaderImage;
            Circle storyHeaderCircle;
            ImagePattern storyHeaderPattern;
            Label storyLabel;
            VBox storyHeaderVB;
            for(int i=0;i<currUserStrangersStories.size();i++) {
                storyHeaderPath=currUserStrangersStories.get(i).sender.filePath;
                storyHeaderStream=new FileInputStream(storyHeaderPath);
                storyHeaderImage=new Image(storyHeaderStream);
                storyHeaderCircle=new Circle(15);
                storyHeaderPattern=new ImagePattern(storyHeaderImage);
                storyHeaderCircle.setFill(storyHeaderPattern);
                storyLabel=new Label(currUserStrangersStories.get(i).sender.username);
                storyHeaderVB=new VBox(2, storyHeaderCircle,storyLabel);
                storiesVBs[i]=storyHeaderVB;
            }
            for (int i=0;i<currUserStrangersStories.size(); i++) {
                int finalI=i;
                storiesVBs[i].addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                    long startTime;
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                            startTime = System.currentTimeMillis();
                        } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                            if (500<System.currentTimeMillis()-startTime) {
                                if(currUserStrangersStories.get(finalI).filePath.length()!=0){
                                    Stage tempStage=Alerts.Alert("Story's Image Downloaded",
                                            "Download Was Successful");
                                    tempStage.show();
                                }
                                saveFile(currUserStrangersStories.get(finalI).filePath,"Story",
                                        String.valueOf(currUserStrangersStories.get(finalI).sender.ID));
                            } else {
                                Stage storyStage=new Stage();
                                try {
                                    showStories(finalI,currUserStrangersStories,currUser,storyStage,primaryStage);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                otherStoriesHB.getChildren().add(new BorderPane(storiesVBs[i]));
            }
        }
        else{
            HBox noStoryLabelHB=new HBox(new StackPane(new Label("No Stories to Show Yet!")));
            otherStoriesHB.getChildren().add(noStoryLabelHB);
            otherStoriesHB.setAlignment(Pos.CENTER);
        }
        storiesSCP.setContent(otherStoriesHB);
        Theme.isDark=currUser.isDark;
        currUserFollowingsVB=finalVB;
    }

    public static void showStories(int finalI, ArrayList<AppStory> currStories, AppUser currUser, Stage storyStage,
                                   Stage primaryStage)
            throws FileNotFoundException {
        if(currStories.size()!=0){
            if(finalI==-1){
                for(int i=0;i<currStories.size();i++){
                    if(!currStories.get(i).seenUsers.contains(currUser)){
                        finalI=i;
                        break;
                    }
                }
            }
            if(finalI==-1){
                finalI=0;
            }
            StackPane[] stories=new StackPane[currStories.size()-finalI];
            StackPane[] root=new StackPane[1];
            HBox HB1;
            int storiesVBMinWidth=500,storiesVBMinHeight=500;
            String storyHeaderPath;
            InputStream tempStream, storyHeaderStream;
            Image tempImage, storyHeaderImage;
            Rectangle tempRect;
            Circle storyHeaderCircle;
            ImagePattern tempPattern, storyHeaderPattern;
            Label storyHeaderLabel;
            HBox storyHeaderHB;
            for(int i=finalI;i<currStories.size();i++){
                long hours=ChronoUnit.HOURS.between(currStories.get(i).sendTime,LocalDateTime.now());
                storyHeaderPath = "C:\\OOP File DataBase\\neutralProfile.jpg";
                if (!currStories.get(i).sender.filePath.equals("")) {
                    storyHeaderPath =currStories.get(i).sender.filePath;
                }
                storyHeaderStream=new FileInputStream(storyHeaderPath);
                storyHeaderImage = new Image(storyHeaderStream);
                storyHeaderCircle = new Circle(20);
                storyHeaderPattern = new ImagePattern(storyHeaderImage);
                storyHeaderCircle.setFill(storyHeaderPattern);
                storyHeaderLabel = new Label(currStories.get(i).sender.username);
                storyHeaderHB = new HBox(10, storyHeaderCircle,storyHeaderLabel);
                tempStream = new FileInputStream(currStories.get(i).filePath);
                tempImage = new Image(tempStream);
                tempRect = new Rectangle(storiesVBMinWidth-5,storiesVBMinHeight);
                tempPattern = new ImagePattern(tempImage);
                tempRect.setFill(tempPattern);
                Label seenSituation=new Label(""),storyNumber=new Label(i+1+"/"+currStories.size()),
                        timeLabel=new Label(hours+" Hour(s) Ago");
                if(currStories.get(i).seenUsers.contains(currUser)){
                    seenSituation=new Label("You Had Viewed This Before!");
                }
                HB1=new HBox(10,timeLabel,storyNumber,seenSituation);
                if (currStories.get(i).caption.length() == 0) {
                    stories[i-finalI]=new StackPane(new VBox(storyHeaderHB,HB1,tempRect));
                } else {
                    stories[i-finalI]=new StackPane(new VBox(storyHeaderHB,HB1,tempRect,
                            new Label(currStories.get(i).sender.username + ": " + currStories.get(i).caption)));
                }
                int finalI2 = finalI,fI=i;
                stories[i-finalI].addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                    long startTime;
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                            startTime = System.currentTimeMillis();
                        } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                            if(500<System.currentTimeMillis()-startTime) {
                                if(currStories.get(fI).sender.equals(currUser)){
                                    AppStory.delete(currStories.get(fI),true);
                                    storyStage.close();
                                    try {
                                        showNeutralCurrUserPage(primaryStage, currUser,new int[]{0});
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    Stage tempStage=Alerts.Alert("Story is Deleted",
                                            "Story Deletion Successful!");
                                    tempStage.show();
                                }

                            }
                            else {
                                if(fI==currStories.size()-1){
                                    storyStage.close();
                                    if(!currStories.get(fI).seenUsers.contains(currUser)){
                                        currStories.get(fI).seenUsers.add(currUser);
                                        currStories.get(fI).seenTimes.add(LocalDateTime.now());
                                        currStories.get(fI).seens.add(new AppSeen(currUser,LocalDateTime.now()));
                                    }
                                }
                                else{
                                    root[0].getChildren().set(0,stories[fI-finalI2 +1]);
                                    if(!currStories.get(fI).seenUsers.contains(currUser)){
                                        currStories.get(fI).seenUsers.add(currUser);
                                        currStories.get(fI).seenTimes.add(LocalDateTime.now());
                                        currStories.get(fI).seens.add(new AppSeen(currUser,LocalDateTime.now()));
                                    }
                                }
                            }
                        }
                    }
                });
            }
            root[0] = new StackPane(stories[0]);
            Scene storyScene=new Scene(root[0]);
            storyStage.setScene(storyScene);
            int col=50;
            Theme.isDark=currUser.isDark;
            if(Theme.isDark){
                storyScene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                storyScene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
            }
            storyStage.show();
        }
    }
    public static void saveFile(String filePath,String fileType,String initialSenderID){
        String s=FilenameUtils.getExtension(filePath);
        File file=new File(filePath);
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();
        String dateTimeString = now.format(formatter);
        File newFile=new File("C:\\OOP\\"+fileType+"_"+initialSenderID+"_"+dateTimeString+"."+s);
        try {
            Files.copy(file.toPath(),newFile.toPath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private static void changePost(Stage primaryStage, AppUser currUser, boolean hasFile, AppPost appPost) {
        try {
            Stage stage=new Stage();
            stage.setTitle("Change Post");
            FileChooser file_chooser = new FileChooser();
            TextField tfCaption=new TextField(appPost.caption);
            tfCaption.setPromptText("Your Caption Goes Here!");
            String[] filePath={""};
            Label label=new Label("No Picture Selected");
            if(hasFile){
                label.setText("This Post Already Has An Image, But You Can Change It");
                filePath[0]=appPost.filePath;
            }
            Button button = new Button("Find a Picture in Your Computer"),
                    b4=new Button("remove Image From This Post"),
                    button1=new Button("Change This Post"),b3=new Button("Delete This Post");
            b3.setOnAction(ActionEvent-> {
                //DataBaseStuff.appPosts.remove(appPost);
                currUser.posts.remove(appPost);
                appPost.isDeleted=true;
                try {
                    Inside.showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
                    //updateCurrUserVB(primaryStage, currUser);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                stage.close();
            });
            b4.setOnAction(ActionEvent-> {
                appPost.filePath="";
                try {
                    Inside.showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
                    //updateCurrUserVB(primaryStage, currUser);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                stage.close();
            });
            button1.setOnAction(ActionEvent-> {
                if(tfCaption.getText().length()==0){
                    label.setText("Post Has to Have Caption!");
                }
                else{
                    appPost.filePath=filePath[0];
                    appPost.caption= tfCaption.getText();
                    try {
                        Inside.showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    stage.close();
                }
            });
            File[] file = new File[1];
            EventHandler<ActionEvent> event1=new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e)
                {
                    file[0]=file_chooser.showOpenDialog(stage);
                    if (file[0] != null) {
                        filePath[0]=file[0].getAbsolutePath();
                        String mimetype= new MimetypesFileTypeMap().
                                getContentType(file[0]);
                        String type = mimetype.split("/")[0];
                        if(type.equals("image")){
                            label.setText("New Image Selected Successfully");
                        }
                        else{
                            label.setText("If You Want to Add A File, It Has to Be An Image!");
                        }
                    }
                }
            };
            button.setOnAction(event1);
            HBox buttonHB=new HBox(10,button1,b3),b1HB=new HBox(10,button,b4);
            VBox vbox = new VBox(30,label,tfCaption,b1HB,buttonHB);
            vbox.setAlignment(Pos.CENTER);
            buttonHB.setAlignment(Pos.CENTER);
            b1HB.setAlignment(Pos.CENTER);
            Scene newProfScene=new Scene(vbox, 400,200);
            int col=40;
            if(Theme.isDark){
                newProfScene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                newProfScene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
            }
            stage.setScene(newProfScene);
            stage.show();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void updateCommentsLikesVB(AppUser currUser,AppUser selectedUser,AppPost currPost,
                                             Stage postStage,boolean showComments) {
        ScrollPane ScrP=new ScrollPane();
        VBox likesVB=new VBox(3),commentsVB=new VBox(3);
        int width=400,VBHeight=500,ScrPHeight=100;
        Button showStatsButton=new Button("Show Post Stats");
        int[] replyTo={0};
        long seconds,minutes,hours,days,weeks;
        if(!showComments){
            if(currPost.likes.size()!=0){
                for(AppSeen i: currPost.likes){
                    seconds=ChronoUnit.SECONDS.between(i.time,LocalDateTime.now());
                    minutes=ChronoUnit.MINUTES.between(i.time,LocalDateTime.now());
                    hours=ChronoUnit.HOURS.between(i.time,LocalDateTime.now());
                    days=ChronoUnit.DAYS.between(i.time,LocalDateTime.now());
                    weeks=ChronoUnit.WEEKS.between(i.time,LocalDateTime.now());
                    Label tempLike;
                    if(seconds<60){
                        tempLike=new Label("Liked by "+i.user.username+" "+seconds+" Seconds Ago");
                    }
                    else if(minutes<60){
                        tempLike=new Label("Liked by "+i.user.username+" "+minutes+" Minutes Ago");
                    }
                    else if(hours<24){
                        tempLike=new Label("Liked by "+i.user.username+" "+hours+" Hours Ago");
                    }
                    else if(days<7){
                        tempLike=new Label("Liked by "+i.user.username+" "+days+" Days Ago");
                    }
                    else{
                        tempLike=new Label("Liked by "+i.user.username+" "+weeks+" Weeks Ago");
                    }
                    likesVB.getChildren().add(tempLike);
                    tempLike.setWrapText(true);
                    tempLike.setTextAlignment(TextAlignment.JUSTIFY);
                    tempLike.setMaxWidth(width-15);
                }
            }
            else{
                likesVB.getChildren().add(new Label("No Likes Yet!"));
            }
            ScrP.setMinSize(width,ScrPHeight);
            ScrP.setMaxSize(width,ScrPHeight);
            ScrP.setContent(likesVB);
            ScrP.setVvalue(1);
            Label viewsLabel=new Label("Views: "+currPost.seens.size()),
                    likesLabel=new Label("Likes: "+currPost.likes.size());
            HBox statsLabelsHB=new HBox(10,viewsLabel,likesLabel);
            commentsLikesVB=new VBox(statsLabelsHB,ScrP,showStatsButton);
            if(!selectedUser.isBusiness){
                showStatsButton.setDisable(true);
            }
            showStatsButton.setOnAction(e-> showStats(currPost.seens,currPost.likes));
        }
        else{
            if(currPost.comments.size()!=0){
                VBox[] commentsVBs=new VBox[currPost.comments.size()];
                int a=0;
                for(AppComment i: currPost.comments){
                    seconds=ChronoUnit.SECONDS.between(i.sendTime,LocalDateTime.now());
                    minutes=ChronoUnit.MINUTES.between(i.sendTime,LocalDateTime.now());
                    hours=ChronoUnit.HOURS.between(i.sendTime,LocalDateTime.now());
                    days=ChronoUnit.DAYS.between(i.sendTime,LocalDateTime.now());
                    weeks=ChronoUnit.WEEKS.between(i.sendTime,LocalDateTime.now());
                    Label timeLabel,nameLabel,textLabel;
                    if(i.replyTo!=0){
                        nameLabel=new Label(i.sender.username+" Replying to "+DataBaseStuff.appComments.get(i.replyTo-1).sender.username+": ");
                    }
                    else {
                        nameLabel=new Label(i.sender.username+": ");
                    }
                    if(seconds<60){
                        timeLabel=new Label(seconds+" Seconds Ago");
                    }
                    else if(minutes<60){
                        timeLabel=new Label(minutes+" Minutes Ago");
                    }
                    else if(hours<24){
                        timeLabel=new Label(hours+" Hours Ago");
                    }
                    else if(days<7){
                        timeLabel=new Label(days+" Days Ago");
                    }
                    else{
                        timeLabel=new Label(weeks+" Weeks Ago");
                    }
                    String s="";
                    if(i.upVotes.contains(currUser)){
                        s="You UpVoted This!";
                    }
                    else if(i.downVotes.contains(currUser)){
                        s="You DownVoted This!";
                    }
                    HBox tempHB=new HBox(5,timeLabel,new Label(i.upVotes.size()+" UpVotes"),
                            new Label(i.downVotes.size()+" DownVotes"),new Label(s));
                    tempHB.setAlignment(Pos.BOTTOM_RIGHT);
                    textLabel=new Label(i.text);
                    textLabel.setWrapText(true);
                    textLabel.setTextAlignment(TextAlignment.JUSTIFY);
                    textLabel.setMaxWidth(width-15);
                    commentsVBs[a]=new VBox(3,nameLabel,textLabel,tempHB);
                    String cssLayout = "-fx-border-color: black;\n" +
                            "-fx-border-insets: 1;\n" +
                            "-fx-border-width: 1;\n";
                    commentsVBs[a].setStyle(cssLayout);
                    commentsVB.getChildren().add(commentsVBs[a]);
                    a++;
                }
                for(int i=0;i<currPost.comments.size();i++){
                    int finalI = i;
                    commentsVBs[i].addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                        long startTime;
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                                startTime = System.currentTimeMillis();
                            } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                                if (500 < System.currentTimeMillis() - startTime) {
                                    String s1="UpVote/Revoke UpVote",s2="DownVote/Revoke DownVote",s3="Reply",s4="Delete";
                                    Stage tempStage=new Stage();
                                    RadioButton r1,r2,r3,r4;
                                    r1=new RadioButton(s1);
                                    r2=new RadioButton(s2);
                                    r3=new RadioButton(s3);
                                    r4=new RadioButton(s4);
                                    ToggleGroup r=new ToggleGroup();
                                    r1.setToggleGroup(r);
                                    r2.setToggleGroup(r);
                                    r3.setToggleGroup(r);
                                    r4.setToggleGroup(r);
                                    VBox radioButtonsVB=new VBox(5,r1,r2,r3,r4);
                                    r.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
                                        public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
                                            RadioButton rb = (RadioButton) r.getSelectedToggle();
                                            if (rb!=null) {
                                                if(rb.getText().equals("UpVote/Revoke UpVote")){
                                                    if(!currPost.comments.get(finalI).upVotes.contains(currUser)){
                                                        currPost.comments.get(finalI).upVotes.add(currUser);
                                                        currPost.comments.get(finalI).downVotes.remove(currUser);
                                                    }
                                                    else{
                                                        currPost.comments.get(finalI).upVotes.remove(currUser);
                                                    }
                                                    updateCommentsLikesVB(currUser,selectedUser,currPost,postStage,true);
                                                    try {
                                                        addSeen=false;
                                                        showPost(currUser,selectedUser,currPost,postStage);
                                                    } catch (FileNotFoundException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                else if(rb.getText().equals("DownVote/Revoke DownVote")){
                                                    if(!currPost.comments.get(finalI).downVotes.contains(currUser)){
                                                        currPost.comments.get(finalI).upVotes.remove(currUser);
                                                        currPost.comments.get(finalI).downVotes.add(currUser);
                                                    }
                                                    else{
                                                        currPost.comments.get(finalI).downVotes.remove(currUser);
                                                    }
                                                    updateCommentsLikesVB(currUser,selectedUser,currPost,postStage,true);
                                                    try {
                                                        addSeen=false;
                                                        showPost(currUser,selectedUser,currPost,postStage);
                                                    } catch (FileNotFoundException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                else if(rb.getText().equals("Reply")){
                                                    if(replyTo[0]==currPost.comments.get(finalI).ID){
                                                        Stage tempAlert=Alerts.Alert("Done!","Replying Canceled!");
                                                        tempAlert.show();
                                                        replyTo[0]=0;
                                                    }
                                                    else{
                                                        replyTo[0]=currPost.comments.get(finalI).ID;
                                                        Stage tempAlert=Alerts.Alert("Done!","Selected Comment to be Replied");
                                                        tempAlert.show();
                                                    }
                                                }
                                                else if(rb.getText().equals("Delete")){
                                                    if(currUser.equals(selectedUser) || currUser.equals(currPost.comments.get(finalI).sender)){
                                                        currPost.comments.get(finalI).isDeleted=true;
                                                        currPost.comments.remove(finalI);
                                                    }
                                                    else{
                                                        Stage tempAlert=Alerts.Alert("Error!", "You Do Not Have The Authority to " +
                                                                "Delete This Comment!");
                                                        tempAlert.show();
                                                    }
                                                    updateCommentsLikesVB(currUser,selectedUser,currPost,postStage,true);
                                                    try {
                                                        addSeen=false;
                                                        showPost(currUser,selectedUser,currPost,postStage);
                                                    } catch (FileNotFoundException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                tempStage.close();
                                            }
                                        }
                                    });
                                    Scene optionsScene=new Scene(radioButtonsVB);
                                    int col=50;
                                    if(Theme.isDark){
                                        optionsScene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                                        optionsScene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
                                    }
                                    tempStage.setScene(optionsScene);
                                    tempStage.show();
                                }
                            }
                        }
                    });
                }
            }
            else{
                commentsVB.getChildren().add(new Label("No Comments Yet!"));
            }
            ScrP.setMinSize(width,ScrPHeight);
            ScrP.setMaxSize(width,ScrPHeight);
            ScrP.setContent(commentsVB);
            ScrP.setVvalue(1);
            TextField messageTF=new TextField();
            messageTF.setPromptText("Enter Your Comment Here!");
            messageTF.setMinSize(width-50,30);
            messageTF.setMaxSize(width-50,30);
            Button submitButton=new Button("Send");
            submitButton.setMinSize(50,30);
            submitButton.setMaxSize(50,30);
            submitButton.setOnAction(actionEvent -> {
                AppComment newComment=new AppComment(currUser,messageTF.getText(),DataBaseStuff.appComments.size()+1,
                        replyTo[0]);
                DataBaseStuff.appComments.add(newComment);
                currPost.comments.add(newComment);
                updateCommentsLikesVB(currUser,selectedUser,currPost,postStage,true);
                try {
                    addSeen=false;
                    showPost(currUser,selectedUser,currPost,postStage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
            commentsLikesVB=new VBox(ScrP,new HBox(messageTF,submitButton));
        }
    }
    public static void showStats(ArrayList<AppSeen> seens,ArrayList<AppSeen> likes){
        int seenMinutes=0,seenHours=0,seenDays=0,seenWeeks=0,seenMonths=0,likesMinutes=0,likesHours=0,likesDays=0,
        likesWeeks=0,likesMonths=0;
        for(AppSeen i:seens){
            if(LocalDateTime.now().minusMinutes(1).isBefore(i.time)){
                seenMinutes++;
            }
            if(LocalDateTime.now().minusHours(1).isBefore(i.time)){
                seenHours++;
            }
            if(LocalDateTime.now().minusDays(1).isBefore(i.time)){
                seenDays++;
            }
            if(LocalDateTime.now().minusWeeks(1).isBefore(i.time)){
                seenWeeks++;
            }
            if(LocalDateTime.now().minusMonths(1).isBefore(i.time)){
                seenMonths++;
            }
        }
        for(AppSeen i:likes){
            if(LocalDateTime.now().minusMinutes(1).isBefore(i.time)){
                likesMinutes++;
            }
            if(LocalDateTime.now().minusHours(1).isBefore(i.time)){
                likesHours++;
            }
            if(LocalDateTime.now().minusDays(1).isBefore(i.time)){
                likesDays++;
            }
            if(LocalDateTime.now().minusWeeks(1).isBefore(i.time)){
                likesWeeks++;
            }
            if(LocalDateTime.now().minusMonths(1).isBefore(i.time)){
                likesMonths++;
            }
        }
        Stage statsStage=new Stage();
        Label lMinLabel=new Label("Likes in the Past Minute: "+likesMinutes);
        Label lHourLabel=new Label("Likes in the Past Hour: "+likesHours);
        Label lDayLabel=new Label("Likes in the Past Day: "+likesDays);
        Label lWeekLabel=new Label("Likes in the Past Week: "+likesWeeks);
        Label lMonLabel=new Label("Likes in the Past Month: "+likesMonths);
        Label sMinLabel=new Label("Views in the Past Minute: "+seenMinutes);
        Label sHourLabel=new Label("Views in the Past Hour: "+seenHours);
        Label sDayLabel=new Label("Views in the Past Day: "+seenDays);
        Label sWeekLabel=new Label("Views in the Past Week: "+seenWeeks);
        Label sMonLabel=new Label("Views in the Past Month: "+seenMonths);
        VBox VB=new VBox(5,sMinLabel,sHourLabel,sDayLabel,sWeekLabel,sMonLabel,lMinLabel,lHourLabel,lDayLabel,lWeekLabel,lMonLabel);
        VB.setAlignment(Pos.CENTER);
        Scene scene=new Scene(VB);
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }
        statsStage.setScene(scene);
        statsStage.show();
    }
    public static void showPost(AppUser currUser,AppUser selectedUser,AppPost currPost,Stage postStage)
            throws FileNotFoundException {
        if(addSeen){
            LocalDateTime seenTime=LocalDateTime.now();
            currPost.seens.add(new AppSeen(currUser,seenTime));
            currPost.seenTimes.add(seenTime);
            currPost.seenUsers.add(currUser);
            addSeen=false;
        }
        int postVBMinWidth=400,postVBMinHeight=600;
        VBox postVB;
        String postHeaderPath="C:\\OOP File DataBase\\neutralProfile.jpg";
        if(!selectedUser.filePath.equals("")){
            postHeaderPath=selectedUser.filePath;
        }
        InputStream tempStream, postHeaderStream;
        Image tempImage,postHeaderImage;
        Rectangle tempRect;
        Circle postHeaderCircle;
        ImagePattern tempPattern,postHeaderPattern;
        Label postHeaderLabel,postFooterLabel;
        HBox postHeaderHB;
        postHeaderStream=new FileInputStream(postHeaderPath);
        postHeaderImage=new Image(postHeaderStream);
        postHeaderCircle=new Circle(20);
        postHeaderPattern=new ImagePattern(postHeaderImage);
        postHeaderCircle.setFill(postHeaderPattern);
        postHeaderLabel=new Label(selectedUser.username);
        postFooterLabel=new Label(selectedUser.username+": "+currPost.caption);
        //postFooterLabel.setMaxWidth(profileVBMinWidth-5);
        postFooterLabel.setWrapText(true);
        postFooterLabel.setTextAlignment(TextAlignment.JUSTIFY);
        postFooterLabel.setMaxWidth(postVBMinWidth-15);
        postHeaderHB=new HBox(10,postHeaderCircle,postHeaderLabel);
        if(currPost.filePath.length()==0){
            postVB=new VBox(postHeaderHB,postFooterLabel);
        }
        else{
            tempStream=new FileInputStream(currPost.filePath);
            tempImage=new Image(tempStream);
            tempRect= new Rectangle(postVBMinWidth-15,postVBMinHeight/2);
            tempPattern=new ImagePattern(tempImage);
            tempRect.setFill(tempPattern);
            postVB=new VBox(postHeaderHB,tempRect,postFooterLabel);
        }
        Button likesButton=new Button();
        likesButton.setStyle(
                "-fx-shape: \"M23.6,0c-3.4,0-6.3,2.7-7.6,5.6C14.7,2.7,11.8,0,8.4,0C3.8,0,0,3.8,0,8.4c0,9.4,9.5,11.9,16,21.2\n" +
                        "        c6.1-9.3,16-12.1,16-21.2C32,3.8,28.2,0,23.6,0z\";"
        );
        Button commentsButton=new Button();
        commentsButton.setShape(new Circle(1.5));
        commentsButton.setMaxSize(3,3);
        HBox likeCommentHB=new HBox(5,likesButton,commentsButton);
        postVB.getChildren().add(likeCommentHB);
        commentsButton.setOnAction(e-> {
            updateCommentsLikesVB(currUser,selectedUser,currPost,postStage,true);
            try {
                showPost(currUser,selectedUser,currPost,postStage);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        //add ScrP[0] and statsButton to postVB
        if(commentsLikesVB==null){
            updateCommentsLikesVB(currUser,selectedUser,currPost,postStage,true);
        }
        commentsLikesVB.setAlignment(Pos.CENTER);
        postVB.getChildren().add(commentsLikesVB);
        Scene scene=new Scene(postVB);
        postStage.setScene(scene);
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }
        postStage.show();
        likesButton.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            long startTime;
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                    startTime = System.currentTimeMillis();
                } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                    if (500 < System.currentTimeMillis() - startTime) {
                        AppSeen newSeen=new AppSeen(currUser,LocalDateTime.now());
                        if(currPost.likes.contains(newSeen)){
                            currPost.likes.remove(newSeen);
                        }
                        else{
                            currPost.likes.add(newSeen);
                        }
                    }
                    updateCommentsLikesVB(currUser,selectedUser,currPost,postStage,false);
                    try {
                        showPost(currUser,selectedUser,currPost,postStage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public static ScrollPane showLikes(AppPost currPost,int commentVBMinWidth,int commentVBMinHeight){
        ScrollPane ScrP=new ScrollPane();
        VBox likesVB=new VBox();
        long seconds,minutes,hours,days,weeks;
        for(AppSeen i: currPost.likes){
            seconds=ChronoUnit.SECONDS.between(i.time,LocalDateTime.now());
            minutes=ChronoUnit.MINUTES.between(i.time,LocalDateTime.now());
            hours=ChronoUnit.HOURS.between(i.time,LocalDateTime.now());
            days=ChronoUnit.DAYS.between(i.time,LocalDateTime.now());
            weeks=ChronoUnit.WEEKS.between(i.time,LocalDateTime.now());
            Label tempLike;
            if(seconds<60){
                tempLike=new Label("Liked by "+i.user.username+" "+seconds+" Seconds Ago");
            }
            else if(minutes<60){
                tempLike=new Label("Liked by "+i.user.username+" "+minutes+" Minutes Ago");
            }
            else if(hours<24){
                tempLike=new Label("Liked by "+i.user.username+" "+hours+" Hours Ago");
            }
            else if(days<7){
                tempLike=new Label("Liked by "+i.user.username+" "+days+" Days Ago");
            }
            else{
                tempLike=new Label("Liked by "+i.user.username+" "+weeks+" Weeks Ago");
            }
            likesVB.getChildren().add(tempLike);
            tempLike.setWrapText(true);
            tempLike.setTextAlignment(TextAlignment.JUSTIFY);
            tempLike.setMaxWidth(commentVBMinWidth-15);
        }
        ScrP.setMinSize(commentVBMinWidth,commentVBMinHeight);
        ScrP.setMaxSize(commentVBMinWidth,commentVBMinHeight);
        ScrP.setContent(likesVB);
        ScrP.setVvalue(1);
        return ScrP;
    }
    public static ScrollPane showComments(AppUser currUser,AppPost currPost,int commentVBMinWidth,int commentVBMinHeight,
                                          Stage postStage){
        ScrollPane ScrP=new ScrollPane();
        VBox[] commentsVBs=new VBox[currPost.comments.size()];
        int a=0;
        VBox commentsVB=new VBox(2);
        long seconds,minutes,hours,days,weeks;
        for(AppComment i: currPost.comments){
            seconds=ChronoUnit.SECONDS.between(i.sendTime,LocalDateTime.now());
            minutes=ChronoUnit.MINUTES.between(i.sendTime,LocalDateTime.now());
            hours=ChronoUnit.HOURS.between(i.sendTime,LocalDateTime.now());
            days=ChronoUnit.DAYS.between(i.sendTime,LocalDateTime.now());
            weeks=ChronoUnit.WEEKS.between(i.sendTime,LocalDateTime.now());
            Label timeLabel,nameLabel,textLabel;
            if(i.replyTo!=0){
                nameLabel=new Label(i.sender.username+" Replying to "+DataBaseStuff.appComments.get(i.replyTo).sender.username+": ");
            }
            else {
                nameLabel=new Label(i.sender.username+": ");
            }
            if(seconds<60){
                timeLabel=new Label(seconds+" Seconds Ago");
            }
            else if(minutes<60){
                timeLabel=new Label(minutes+" Minutes Ago");
            }
            else if(hours<24){
                timeLabel=new Label(hours+" Hours Ago");
            }
            else if(days<7){
                timeLabel=new Label(days+" Days Ago");
            }
            else{
                timeLabel=new Label(weeks+" Weeks Ago");
            }
            String s="";
            if(i.upVotes.contains(currUser)){
                s="You UpVoted This!";
            }
            else if(i.downVotes.contains(currUser)){
                s="You DownVoted This!";
            }
            HBox tempHB=new HBox(5,timeLabel,new Label(i.upVotes.size()+" UpVotes"),
                    new Label(i.downVotes.size()+" DownVotes"),new Label(s));
            tempHB.setAlignment(Pos.BOTTOM_RIGHT);
            textLabel=new Label(i.text);
            textLabel.setWrapText(true);
            textLabel.setTextAlignment(TextAlignment.JUSTIFY);
            textLabel.setMaxWidth(commentVBMinWidth-15);
            commentsVBs[a]=new VBox(3,nameLabel,textLabel,tempHB);
            commentsVB.getChildren().add(commentsVBs[a]);
            a++;
        }
        ScrP.setMinSize(commentVBMinWidth,commentVBMinHeight);
        ScrP.setMaxSize(commentVBMinWidth,commentVBMinHeight);
        ScrP.setContent(commentsVB);
        ScrP.setVvalue(1);
        for(int i=0;i<currPost.comments.size();i++){
            int finalI = i;
            commentsVBs[i].addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                long startTime;
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                        startTime = System.currentTimeMillis();
                    } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                        if (500 < System.currentTimeMillis() - startTime) {
                            String s1="UpVote",s2="DownVote",s3="Reply",s4="Delete";
                            Stage tempStage=new Stage();
                            RadioButton r1,r2,r3,r4;
                            int f=0;
                            if(currPost.comments.get(finalI).upVotes.contains(currUser)){
                                s1="Revoke Upvote";
                                s2="Revoke Upvote And DownVote This Comment";
                                f=1;
                            }
                            else if(currPost.comments.get(finalI).downVotes.contains(currUser)){
                                s1="Revoke DownVote And Upvote This Comment";
                                s2="Revoke DownVote";
                                f=2;
                            }
                            r1=new RadioButton(s1);
                            r2=new RadioButton(s2);
                            r3=new RadioButton(s3);
                            r4=new RadioButton(s4);
                            ToggleGroup r=new ToggleGroup();
                            r1.setToggleGroup(r);
                            r2.setToggleGroup(r);
                            r3.setToggleGroup(r);
                            r4.setToggleGroup(r);
                            VBox radioButtonsVB=new VBox(5,r1,r2,r3);
                            if(currPost.comments.get(finalI).sender.equals(currUser) || currPost.sender.equals(currUser)){
                                radioButtonsVB.getChildren().add(r4);
                            }
                            if(f==0){
                                if(r1.isSelected()){
                                    currPost.comments.get(finalI).upVotes.add(currUser);
                                }
                                if(r2.isSelected()){
                                    currPost.comments.get(finalI).downVotes.add(currUser);
                                }
                            }
                            else if(f==1){
                                if(r1.isSelected()){
                                    currPost.comments.get(finalI).upVotes.remove(currUser);
                                }
                                if(r2.isSelected()){
                                    currPost.comments.get(finalI).downVotes.add(currUser);
                                    currPost.comments.get(finalI).upVotes.remove(currUser);
                                }
                            }
                            else{
                                if(r1.isSelected()){
                                    currPost.comments.get(finalI).downVotes.remove(currUser);
                                    currPost.comments.get(finalI).upVotes.add(currUser);
                                }
                                if(r2.isSelected()){
                                    currPost.comments.get(finalI).downVotes.remove(currUser);
                                }
                            }
                            if(r1.isSelected()){
                                try {
                                    addSeen=false;
                                    showPost(currUser,currPost.sender,currPost,postStage);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if(r2.isSelected()){
                                try {
                                    addSeen=false;
                                    showPost(currUser,currPost.sender,currPost,postStage);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if(r3.isSelected()){

                            }
                            else if(r4.isSelected()){
                                currPost.comments.get(finalI).isDeleted=true;
                                currPost.comments.remove(finalI);
                                try {
                                    addSeen=false;
                                    showPost(currUser,currPost.sender,currPost,postStage);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            Scene tempScene1=new Scene(radioButtonsVB);
                            Stage tempStage1=new Stage();
                            int col=50;
                            if(Theme.isDark){
                                tempScene1.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                                tempScene1.getRoot().setStyle("-fx-base:rgb(1,1,1)");
                            }
                            tempStage1.setScene(tempScene1);
                            tempStage1.show();
                        }
                    }
                }
            });
        }
        return ScrP;
    }
    private static void changeCurrUsersEmail(Stage stage, AppUser currUser) {
        Stage stage1=new Stage();
        VBox vBox1=new VBox(10);
        stage1.setTitle("Change Email Address");
        TextField tf1=new TextField(currUser.emailAddress);
        Label l1=new Label("Enter the Email Adress"),errorLabel=new Label();
        Button button1=new Button("Close"),b2=new Button("Submit");
        StackPane sp1=new StackPane(button1),sp2=new StackPane(b2);
        HBox hb=new HBox(10,sp2,sp1);
        button1.setOnAction(ActionEvent-> stage1.close());
        b2.setOnAction(ActionEvent-> {
            if(Pattern.matches("[a-zA-Z0-9.]+@[a-zA-Z]+.com",tf1.getText())){
                currUser.emailAddress=tf1.getText();
                stage1.close();
                stage.close();
            }
            else{
                errorLabel.setText("Please Enter A Valid Email Address!");
            }
        });
        vBox1.getChildren().addAll(new StackPane(l1),new StackPane(tf1),new StackPane(errorLabel),
                new StackPane(hb));
        tf1.setPrefWidth(200);
        hb.setAlignment(Pos.CENTER);
        Scene scene=new Scene(vBox1, 300,120);
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }
        stage1.setScene(scene);
        stage1.show();
    }
    private static void changeCurrUsersAcc(Stage primaryStage,Stage stage, AppUser currUser) {
        Stage stage1=new Stage();
        Label selectAccType=new Label("Choose Your Account Privacy Type:"),
                errorLabel=new Label();
        Button submit=new Button("Submit"),button1=new Button("Close");
        RadioButton AccTypes1=new RadioButton("Private"),AccTypes2=new RadioButton("Public");
        ToggleGroup Accs=new ToggleGroup();
        HBox AccHB=new HBox(10,AccTypes1,AccTypes2);
        AccTypes1.setToggleGroup(Accs);
        AccTypes2.setToggleGroup(Accs);
        if(currUser.isPrivate){
            AccTypes1.setSelected(true);
        }
        else{
            AccTypes2.setSelected(true);
        }
        submit.setOnAction(ActionEvent->{
            if(AccTypes1.isSelected()) {
                currUser.isPrivate=true;
                stage.close();
                stage1.close();
            }
            else if(AccTypes2.isSelected()){
                currUser.isPrivate=false;
                for(AppUser i:currUser.followRequests){
                    currUser.acceptFollow(i);
                    currUser.followRequests.remove(i);
                }
                try {
                    showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
                } catch (FileNotFoundException e6) {
                    e6.printStackTrace();
                }
                stage.close();
                stage1.close();
            }
            else{
                errorLabel.setText("You Should Choose An Account Privacy Type!");
            }
        });
        button1.setOnAction(ActionEvent-> stage1.close());
        HBox HB=new HBox(10,submit,button1);
        VBox changeAccType=new VBox(10,new StackPane(selectAccType),AccHB,
                new StackPane(errorLabel),HB);
        HB.setAlignment(Pos.CENTER);
        AccHB.setAlignment(Pos.CENTER);
        Scene scene=new Scene(changeAccType,300,150);
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }
        stage1.setScene(scene);
        stage1.show();
    }
    private static void changeCurrUsersTwoStep(Stage stage, AppUser currUser) {
        Stage stage1=new Stage();
        Label selectAccType=new Label("Choose Your Account's Verification Style:"),
                errorLabel=new Label();
        Button submit=new Button("Submit"),button1=new Button("Close");
        RadioButton AccTypes1=new RadioButton("2-Step Verification")
                ,AccTypes2=new RadioButton("Regular Verification");
        ToggleGroup Accs=new ToggleGroup();
        HBox AccHB=new HBox(10,AccTypes1,AccTypes2);
        AccTypes1.setToggleGroup(Accs);
        AccTypes2.setToggleGroup(Accs);
        if(currUser.twoStep){
            AccTypes1.setSelected(true);
        }
        else{
            AccTypes2.setSelected(true);
        }
        submit.setOnAction(ActionEvent->{
            if(AccTypes1.isSelected()) {
                currUser.twoStep=true;
                stage.close();
                stage1.close();
            }
            else if(AccTypes2.isSelected()){
                currUser.twoStep=false;
                stage.close();
                stage1.close();
            }
            else{
                errorLabel.setText("You Should An Account Privacy Type!");
            }
        });
        button1.setOnAction(ActionEvent-> stage1.close());
        HBox HB=new HBox(10,submit,button1);
        VBox changeAccType=new VBox(10,new StackPane(selectAccType),AccHB,
                new StackPane(errorLabel),HB);
        HB.setAlignment(Pos.CENTER);
        AccHB.setAlignment(Pos.CENTER);
        Scene scene=new Scene(changeAccType,300,150);
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }
        stage1.setScene(scene);
        stage1.show();
    }
    private static void changeCurrUsersAccType(Stage stage, AppUser currUser) {
        Stage stage1=new Stage();
        Label selectAccType=new Label("Choose Your Account Type:"),
                errorLabel=new Label();
        Button submit=new Button("Submit"),button1=new Button("Close");
        RadioButton AccTypes1=new RadioButton("Business"),AccTypes2=new RadioButton("Regular");
        ToggleGroup Accs=new ToggleGroup();
        HBox AccHB=new HBox(10,AccTypes1,AccTypes2);
        AccTypes1.setToggleGroup(Accs);
        AccTypes2.setToggleGroup(Accs);
        if(currUser.isBusiness){
            AccTypes1.setSelected(true);
        }
        else{
            AccTypes2.setSelected(true);
        }
        submit.setOnAction(ActionEvent->{
            if(AccTypes1.isSelected()) {
                currUser.isBusiness=true;
                stage.close();
                stage1.close();
            }
            else if(AccTypes2.isSelected()){
                currUser.isBusiness=false;
                stage.close();
                stage1.close();
            }
            else{
                errorLabel.setText("You Should An Account Type!");
            }
        });
        button1.setOnAction(ActionEvent-> stage1.close());
        HBox HB=new HBox(10,submit,button1);
        VBox changeAccType=new VBox(10,new StackPane(selectAccType),AccHB,
                new StackPane(errorLabel),HB);
        HB.setAlignment(Pos.CENTER);
        AccHB.setAlignment(Pos.CENTER);
        Scene scene=new Scene(changeAccType,300,150);
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }
        stage1.setScene(scene);
        stage1.show();
    }

    private static void changeCurrUsersSecQuestion(Stage stage, AppUser currUser) {
        Stage stage1=new Stage();
        Label selectSecurityQuestion=new Label("Choose Your Security Question:"),
                answerLabel=new Label("Security Question's Answer"),
                hintLabel=new Label("Security Question's Hint"), errorLabel=new Label();
        TextField tfHint=new TextField(currUser.securityHint),
                tfAnswer=new TextField(currUser.securityAnswer);
        VBox answer=new VBox(10,answerLabel,tfAnswer),hint=new VBox(10,hintLabel,tfHint);
        HBox secQHB=new HBox(10,answer,hint);
        Button submit=new Button("Submit"),button1=new Button("Close");
        RadioButton[] security=new RadioButton[7];
        ToggleGroup questions=new ToggleGroup();
        VBox secQVB=new VBox(5);
        for(int j=0;j<7;j++){
            security[j]=new RadioButton(securityQuestions[j]);
            security[j].setToggleGroup(questions);
            secQVB.getChildren().addAll(security[j]);
        }
        submit.setOnAction(ActionEvent->{
            int t=-1;
            for(int k=0;k<7;k++){
                if(security[k].isSelected()) {
                    if(tfAnswer.getText().equals("")){
                        errorLabel.setText("You Should Enter An Answer to the Security Question!");
                    }
                    else{
                        if(tfHint.getText().equals("")){
                            errorLabel.setText("You Should Enter A Hint to the Security Question!");
                        }
                        else{
                            currUser.securityQuestion=securityQuestions[k];
                            currUser.securityAnswer=tfAnswer.getText();
                            currUser.securityHint=tfHint.getText();
                            stage.close();
                            stage1.close();
                        }
                    }
                    t=k;
                    break;
                }
            }
            if(t==-1){
                errorLabel.setText("You Should Choose A Security Question!");
            }
        });
        button1.setOnAction(ActionEvent-> stage1.close());
        HBox HB=new HBox(10,submit,button1);
        VBox changeSecurity=new VBox(10,new StackPane(selectSecurityQuestion),secQVB,secQHB,
                new StackPane(errorLabel),HB);
        HB.setAlignment(Pos.CENTER);
        Scene scene=new Scene(changeSecurity);
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }
        stage1.setScene(scene);
        stage1.show();
    }
    private static void changeCurrUsersPassword(Stage stage, AppUser currUser) {
        Stage stage1=new Stage();
        Label passwordLabel=new Label("Please Enter Your Password"),
                passwordLabel2=new Label("Please Re-Enter Your Password"),
        errorLabel=new Label("");
        Button changePassword=new Button("Change Password"),button1=new Button("Close");
        button1.setOnAction(ActionEvent-> stage1.close());
        PasswordField pfPassword=new PasswordField(),pfPassword2=new PasswordField();
        pfPassword.setPrefWidth(180);
        pfPassword2.setPrefWidth(180);
        changePassword.setOnAction(ActionEvent-> {
            if(pfPassword.getText().length()<8){
                errorLabel.setText("Your Password Should Have At Least 8 Characters!");
            }
            else if(!pfPassword.getText().equals(pfPassword2.getText())){
                errorLabel.setText("Your Passwords Are Different!");
            }
            else if(pfPassword.getText().equals(currUser.password)){
                errorLabel.setText("This is Your Current Password!");
            }
            else{
                currUser.password=pfPassword.getText();
                stage1.close();
                stage.close();
            }
        });
        VBox passVB=new VBox(10,passwordLabel,pfPassword),
                passVB2=new VBox(10,passwordLabel2,pfPassword2);
        HBox passHB=new HBox(10,passVB,passVB2),HB=new HBox(20,changePassword,button1);
        VBox changePasswordVB=new VBox(20,passHB,new StackPane(errorLabel),HB);
        HB.setAlignment(Pos.CENTER);
        Scene scene=new Scene(changePasswordVB);
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }
        errorLabel.setTextFill(Color.web(String.valueOf(Color.RED)));
        stage1.setScene(scene);
        stage1.show();
    }
    private static void changeCurrUsersName(Stage primaryStage,Stage stage, AppUser currUser) {
        Stage stage1=new Stage();
        VBox vBox1=new VBox(10);
        stage1.setTitle("Change Name");
        TextField tf1=new TextField(currUser.name);
        Label l1=new Label("Enter the Name");
        Button button1=new Button("Close"),b2=new Button("Submit");
        StackPane sp1=new StackPane(button1),sp2=new StackPane(b2);
        HBox hb=new HBox(10,sp2,sp1);
        button1.setOnAction(ActionEvent-> stage1.close());
        b2.setOnAction(ActionEvent-> {
            currUser.name=tf1.getText();
            stage1.close();
            try {
                Inside.showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
                //updateCurrUserVB(primaryStage, currUser);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            stage.close();
        });
        vBox1.getChildren().addAll(new StackPane(l1),new StackPane(tf1),new StackPane(hb));
        //vBox1.setAlignment(Pos.CENTER);
        tf1.setPrefWidth(200);
        hb.setAlignment(Pos.CENTER);
        Scene scene=new Scene(vBox1, 300,100);
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }
        stage1.setScene(scene);
        stage1.show();
    }
    public static void makePost(Stage primaryStage,AppUser currUser){
        try {
            Stage stage=new Stage();
            stage.setTitle("Make Post");
            FileChooser file_chooser = new FileChooser();
            TextField tfCaption=new TextField();
            tfCaption.setPromptText("Your Caption Goes Here!");
            String[] filePath={""};
            Label label = new Label("No Picture Selected");
            Button button = new Button("Find a Picture in Your Computer"),
            button1=new Button("Make This Post");
            button1.setOnAction(ActionEvent-> {
                if(tfCaption.getText().length()==0){
                    label.setText("Post Has to Have Caption!");
                }
                else{
                    AppPost tempPost=new AppPost(currUser,tfCaption.getText(),filePath[0],
                            DataBaseStuff.appPosts.size()+1);
                    DataBaseStuff.appPosts.add(tempPost);
                    currUser.posts.add(tempPost);
                    try {
                        Inside.showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    stage.close();
                }
            });
            File[] file = new File[1];
            EventHandler<ActionEvent> event1=new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e)
                {
                    file[0]=file_chooser.showOpenDialog(stage);
                    if (file[0] != null) {
                        filePath[0]=file[0].getAbsolutePath();
                        String mimetype= new MimetypesFileTypeMap().
                                getContentType(file[0]);
                        String type = mimetype.split("/")[0];
                        if(type.equals("image")){
                            label.setText("Image Selected Successfully");
                        }
                        else{
                            label.setText("If You Want to Add A File, It Has to Be An Image!");
                        }
                    }
                }
            };
            button.setOnAction(event1);
            VBox vbox = new VBox(30,label,tfCaption,button,button1);
            vbox.setAlignment(Pos.CENTER);
            Scene newProfScene=new Scene(vbox, 300,200);
            int col=40;
            if(Theme.isDark){
                newProfScene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                newProfScene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
            }
            stage.setScene(newProfScene);
            stage.show();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public static void makeStory(Stage primaryStage,AppUser currUser){
        try {
            Stage stage=new Stage();
            stage.setTitle("Make Story");
            FileChooser file_chooser = new FileChooser();
            TextField tfCaption=new TextField("");
            tfCaption.setPromptText("Your Caption Goes Here!");
            String[] filePath={""};
            Label label = new Label("No Picture Selected");
            Button button = new Button("Find a Picture in Your Computer"),
                    button1=new Button("Make This Story");
            button1.setOnAction(ActionEvent-> {
                if(filePath[0].length()==0){
                    label.setText("Story Has to Have An Image!");
                }
                else{
                    AppStory tempStory=new AppStory(currUser,filePath[0],tfCaption.getText(),
                            DataBaseStuff.appStories.size()+1);
                    DataBaseStuff.appStories.add(tempStory);
                    currUser.stories.add(tempStory);
                    try {
                        Inside.showNeutralCurrUserPage(primaryStage,currUser,new int[]{0});
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    stage.close();
                }
            });
            File[] file = new File[1];
            EventHandler<ActionEvent> event1=new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e)
                {
                    file[0]=file_chooser.showOpenDialog(stage);
                    if (file[0] != null) {
                        filePath[0]=file[0].getAbsolutePath();
                        String mimetype= new MimetypesFileTypeMap().
                                getContentType(file[0]);
                        String type = mimetype.split("/")[0];
                        if(type.equals("image")){
                            label.setText("Image Selected Successfully");
                        }
                        else{
                            label.setText("If You Want to Add A File, It Has to Be An Image!");
                        }
                    }
                }
            };
            button.setOnAction(event1);
            VBox vbox = new VBox(30,label,tfCaption,button,button1);
            vbox.setAlignment(Pos.CENTER);
            Scene newProfScene=new Scene(vbox, 300,200);
            int col=40;
            if(Theme.isDark){
                newProfScene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
                newProfScene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
            }
            stage.setScene(newProfScene);
            stage.show();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void showInitialPage(Stage primaryStage,AppUser currUser){
        primaryStage.setResizable(false);
    }
    public static void searchUser(){
        SearchUser SU=new SearchUser();
        Stage stage=new Stage();
        SU.start(stage);

    }
    public static void logOut(AppUser currUser) throws SQLException {
        currUser.lastSeen=LocalDateTime.now();
        Entering.menuNumber=1;
        DataBaseStuff.saveDB();
        Theme.isDark=false;
        Stage enteringStage=new Stage();
        Entering.signup(enteringStage);
    }
    public static void setSelectedUser(AppUser selectedUser) {
        selUser=selectedUser;
    }

    public static void showProfile(AppUser currentUser, AppUser selectedUser, Stage profileStage) throws FileNotFoundException {
        if(currentUser==null){
            currentUser=actualCurrentUser;
        }
        Label userNameLabel=new Label(selectedUser.username),fiLabel=new Label("Followings"),
                fwLabel=new Label("Followers"),poLabel=new Label("Posts"),
                postsLabel=new Label(String.valueOf(selectedUser.posts.size())),
                followerLabel=new Label(String.valueOf(selectedUser.followers.size())),
                followingsLabel=new Label(String.valueOf(selectedUser.followings.size())),
                bioLabel=new Label(selectedUser.bio),nameLabel=new Label(selectedUser.name);
        VBox poVB=new VBox(5,poLabel,postsLabel),fwVB=new VBox(5,fwLabel,followerLabel),postsVB=new VBox(),
                fiVB=new VBox(5,fiLabel,followingsLabel);
        ScrollPane ScrP=new ScrollPane();
        nameLabel.setFont(Font.font(15));
        int profileVBMinWidth=400,profileVBMinHeight=600;
        InputStream stream1=new FileInputStream("C:\\OOP File DataBase\\neutralProfile.jpg");
        Button followButton,messageButton=new Button("Message");
        boolean flag=true;
        if(selectedUser.followers.contains(currentUser)){
            followButton=new Button("Unfollow");
            AppUser finalCurrentUser = currentUser;
            followButton.setOnAction(e-> {
                finalCurrentUser.unfollow(selectedUser);
                try {
                    showNeutralCurrUserPage(actualPrimaryStage,finalCurrentUser,new int[]{0});
                    showProfile(finalCurrentUser,selectedUser,profileStage);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            });
        }
        else if(!selectedUser.equals(currentUser)){
            if(selectedUser.isPrivate){
                if(selectedUser.followRequests.contains(currentUser)){
                    followButton=new Button("Revoke Follow Request");
                    AppUser finalCurrentUser2 = currentUser;
                    followButton.setOnAction(e-> {
                        finalCurrentUser2.unfollow(selectedUser);
                        try {
                            showNeutralCurrUserPage(actualPrimaryStage,finalCurrentUser2,new int[]{0});
                            showProfile(finalCurrentUser2,selectedUser,profileStage);
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                else{
                    followButton=new Button("Send Follow Request");
                    AppUser finalCurrentUser1 = currentUser;
                    followButton.setOnAction(e-> {
                        finalCurrentUser1.follow(selectedUser);
                        try {
                            showNeutralCurrUserPage(actualPrimaryStage,finalCurrentUser1,new int[]{0});
                            showProfile(finalCurrentUser1,selectedUser,profileStage);
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                flag=false;
                messageButton.setDisable(true);
            }
            else{
                followButton=new Button("Follow");
                AppUser finalCurrentUser3 = currentUser;
                followButton.setOnAction(e-> {
                    finalCurrentUser3.follow(selectedUser);
                    try {
                        showNeutralCurrUserPage(actualPrimaryStage,finalCurrentUser3,new int[]{0});
                        showProfile(finalCurrentUser3,selectedUser,profileStage);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        }
        else{
            followButton=new Button("This is You!");
            followButton.setDisable(true);
        }
        ScrP.setMinSize(profileVBMinWidth,profileVBMinHeight/2+100);
        ScrP.setMaxSize(profileVBMinWidth,profileVBMinHeight/2+100);
        if(flag){
            if(selectedUser.posts.size()!=0){
                VBox[] postsVBs=new VBox[selectedUser.posts.size()];
                String postHeaderPath="C:\\OOP File DataBase\\neutralProfile.jpg";
                if(!selectedUser.filePath.equals("")){
                    postHeaderPath=selectedUser.filePath;
                }
                InputStream tempStream, postHeaderStream;
                Image tempImage,postHeaderImage;
                Rectangle tempRect;
                Circle postHeaderCircle;
                ImagePattern tempPattern,postHeaderPattern;
                Label postHeaderLabel,postFooterLabel;
                HBox postHeaderHB;
                for(int i=0;i<selectedUser.posts.size();i++){
                    //i.seens.
                    postHeaderStream=new FileInputStream(postHeaderPath);
                    postHeaderImage=new Image(postHeaderStream);
                    postHeaderCircle=new Circle(20);
                    postHeaderPattern=new ImagePattern(postHeaderImage);
                    postHeaderCircle.setFill(postHeaderPattern);
                    postHeaderLabel=new Label(selectedUser.username);
                    postFooterLabel=new Label(selectedUser.username+": "+selectedUser.posts.get(i).caption);
                    //postFooterLabel.setMaxWidth(profileVBMinWidth-5);
                    postFooterLabel.setWrapText(true);
                    postFooterLabel.setTextAlignment(TextAlignment.JUSTIFY);
                    postFooterLabel.setMaxWidth(profileVBMinWidth-15);
                    postHeaderHB=new HBox(10,postHeaderCircle,postHeaderLabel);
                    if(selectedUser.posts.get(i).filePath.length()==0){
                        postsVBs[selectedUser.posts.size()-1-i]=new VBox(postHeaderHB,postFooterLabel);
                    }
                    else{
                        tempStream=new FileInputStream(selectedUser.posts.get(i).filePath);
                        tempImage=new Image(tempStream);
                        tempRect= new Rectangle(profileVBMinWidth-15,profileVBMinHeight/2);
                        tempPattern=new ImagePattern(tempImage);
                        tempRect.setFill(tempPattern);
                        postsVBs[selectedUser.posts.size()-1-i]=new VBox(postHeaderHB,tempRect,postFooterLabel);
                    }
                }
                for(int i=0;i<selectedUser.posts.size();i++){
                    int finalI = i;
                    AppUser finalCurrentUser6 = currentUser;
                    postsVBs[i].addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                        long startTime;
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            if(mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)){
                                startTime=System.currentTimeMillis();
                            }
                            else if(mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)){
                                if(500<System.currentTimeMillis()-startTime){
                                    if(selectedUser.posts.get(selectedUser.posts.size()-1-finalI).filePath.length()!=0){
                                        saveFile(selectedUser.posts.get(selectedUser.posts.size()-1-finalI).filePath,
                                                "Post", String.valueOf(selectedUser.ID));
                                        Stage stage2=Alerts.Alert("Post's Image Download!!",
                                                "Post's Image Downloaded Successfully!");
                                        stage2.show();
                                    }
                                }
                                else{
                                    try {
                                        commentsLikesVB=null;
                                        addSeen=true;
                                        showPost(finalCurrentUser6,selectedUser,
                                                selectedUser.posts.get(selectedUser.posts.size()-1-finalI),new Stage());
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                    postsVB.getChildren().add(new BorderPane(postsVBs[i]));
                }
                ScrP.setContent(postsVB);
            }
            else{
                HBox noPostHB=new HBox(new Label("No Posts Here Yet!"));
                noPostHB.setAlignment(Pos.CENTER);
                ScrP.setContent(noPostHB);
            }
        }
        else{
            HBox privateAccountHB=new HBox(new Label("This Account is Private!\nFollow to View the Posts!"));
            privateAccountHB.setAlignment(Pos.CENTER);
            ScrP.setContent(privateAccountHB);
        }
        HBox buttonsHB=new HBox(5,followButton,messageButton);
        String filePath="C:\\OOP File DataBase\\neutralProfile.jpg";
        if(!selectedUser.filePath.equals("")){
            filePath=selectedUser.filePath;
            stream1=new FileInputStream(selectedUser.filePath);
        }
        Image image1=new Image(stream1);
        Circle circle= new Circle(20);
        ImagePattern imagePattern = new ImagePattern(image1);
        circle.setFill(imagePattern);
        HBox folHB=new HBox(20,circle,poVB,fwVB,fiVB);
        userNameLabel.setFont(Font.font("Times new roman",15));
        String finalFilePath = filePath;
        boolean finalFlag = flag;
        AppUser finalCurrentUser4 = currentUser;
        circle.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            long startTime;
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)){
                    startTime=System.currentTimeMillis();
                }
                else if(mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)){
                    if(500<System.currentTimeMillis()-startTime){
                        Stage stage2=Alerts.Alert("Picture Downloaded Successfully!","Profile Picture is Downloaded!");
                        stage2.show();
                        saveFile(finalFilePath,"Profile Picture",String.valueOf(selectedUser.ID));
                    }
                    else{
                        if(finalFlag){
                            try{
                                ArrayList<AppStory> stories=new ArrayList<>();
                                for(AppStory i:selectedUser.stories){
                                    if(!AppStory.delete(i,false)){
                                        stories.add(i);
                                    }
                                }
                                showStories(-1,stories,finalCurrentUser4,new Stage(),actualPrimaryStage);
                            }
                            catch(FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        StackPane usernameSP=new StackPane(userNameLabel),sp3=new StackPane(circle),nameLabelP=new StackPane(nameLabel),
                bioLabelP=new StackPane(bioLabel);
        AppUser finalCurrentUser5 = currentUser;
        if(selectedUser.blockedUsers.contains(currentUser) || currentUser.blockedUsers.contains(selectedUser)){
            messageButton.setDisable(true);
        }
        messageButton.setOnAction(e-> {
            AppChat newChat=new AppChat();
            newChat.chatID=selectedUser.ID;
            newChat.name=selectedUser.username;
            newChat.filePath=selectedUser.filePath;
            if(actualCurrentUser.chats.contains(newChat)){
                for(AppChat j:actualCurrentUser.chats){
                    if(j.equals(newChat)){
                        try {
                            updateInsideChatVB(actualPrimaryStage,actualCurrentUser,j);
                            showInsideChat(actualPrimaryStage);
                        } catch (FileNotFoundException e3) {
                            e3.printStackTrace();
                        }
                    }
                }
            }
            else if(actualCurrentUser.pinnedChats.contains(newChat)){
                for(AppChat j:actualCurrentUser.pinnedChats){
                    if(j.equals(newChat)){
                        try {
                            updateInsideChatVB(actualPrimaryStage,actualCurrentUser,j);
                            showInsideChat(actualPrimaryStage);
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
            else{
                if(actualCurrentUser.equals(selectedUser)){
                    sendChatMessage(actualPrimaryStage,actualCurrentUser, "You Can Save Messages Here!",
                            "", 0, 0, newChat, 1);
                }
                else{
                    sendChatMessage(actualPrimaryStage,actualCurrentUser, "Hi There!",
                            "", 0, 0, newChat, 0);
                }
                actualCurrentUser.chats.add(0,newChat);
                try {
                    updateCurrUserNeutralMessagesVB(actualPrimaryStage,actualCurrentUser,new int[]{0},new int[]{0});
                    updateInsideChatVB(actualPrimaryStage,actualCurrentUser,newChat);
                    showInsideChat(actualPrimaryStage);
                } catch (FileNotFoundException e2) {
                    e2.printStackTrace();
                }
            }
            profileStage.close();
        });
        VBox finalVB=new VBox(5,usernameSP,sp3,folHB,nameLabelP,bioLabelP,buttonsHB,ScrP);
        folHB.setAlignment(Pos.CENTER);
        buttonsHB.setAlignment(Pos.CENTER);
        Scene scene=new Scene(finalVB);
        finalVB.setMinSize(profileVBMinWidth,profileVBMinHeight);
        finalVB.setMaxSize(profileVBMinWidth,profileVBMinHeight);
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }
        profileStage.setScene(scene);
        profileStage.setTitle("Profile of "+selectedUser.username);
        profileStage.show();
    }
}
class Alerts{
    public static String inputString1;
    public static Stage Alert(String Title, String alert) {
        Stage stage=new Stage();
        VBox vBox=new VBox(10);
        stage.setTitle(Title);
        Label label1=new Label(alert);
        Button button1=new Button("Close");
        button1.setOnAction(ActionEvent-> stage.close());
        vBox.getChildren().addAll(label1,button1);
        vBox.setAlignment(Pos.CENTER);
        Scene scene=new Scene(vBox, 350,100);
        int col=50;
        if(Theme.isDark){
            scene.setFill(Color.web(String.valueOf(Color.rgb(col,col,col))));
            scene.getRoot().setStyle("-fx-base:rgb(1,1,1)");
        }
        stage.setScene(scene);
        return stage;
    }
    public static Stage OneInputDialog(String title,String name){
        Stage stage=new Stage();
        VBox vBox1=new VBox(10);
        stage.setTitle(title);
        TextField tf1=new TextField();
        Label l1=new Label(name);
        Button button1=new Button("Close"),b2=new Button("Submit");
        StackPane sp1=new StackPane(button1),sp2=new StackPane(b2);
        HBox hb=new HBox(10,sp2,sp1);
        button1.setOnAction(ActionEvent-> stage.close());
        b2.setOnAction(ActionEvent-> {
            inputString1=tf1.getText();
            stage.close();
        });
        vBox1.getChildren().addAll(new StackPane(l1),new StackPane(tf1),new StackPane(hb));
        //vBox1.setAlignment(Pos.CENTER);
        tf1.setPrefWidth(200);
        hb.setAlignment(Pos.CENTER);
        stage.setScene(new Scene(vBox1, 300,100));
        return stage;
    }
}
class AppMessage{
    AppUser sender;
    String text="";
    String stringID="";//"1_-1"
    int senderID,receiverID;
    int ID;
    LocalDateTime sendTime;
    ArrayList<AppUser> seen=new ArrayList<>(),isDeletedFor=new ArrayList<>();
    String seenString="",isDeletedForString="";
    int replyTo,forwardFrom;
    boolean isDeleted=false,hasBeenForwarded=false;
    String filePath="";
    AppMessage(AppUser sendER,String sID, int intID,int replyTo,int forwardFrom,String text,String filePath){
        this.stringID=sID;
        String[] temp=stringID.split("_");
        this.senderID=Integer.parseInt(temp[0]);
        this.receiverID=Integer.parseInt(temp[1]);
        ID=intID;
        this.sender=sendER;
        this.sendTime=LocalDateTime.now();
        if(!filePath.equals("")){
            this.filePath=filePath;
        }
        this.text=text;
        this.seen.add(sendER);
        this.replyTo=replyTo;
        this.forwardFrom=forwardFrom;
    }
    AppMessage(String text,String stringID,int senderID,int receiverID,int ID,LocalDateTime sendTime,String seenString,
               String isDeletedForString,int replyTo,int forwardFrom,boolean isDeleted,boolean hasBeenForwarded,String filePath){
        this.text=text;
        this.stringID=stringID;
        this.senderID=senderID;
        this.receiverID=receiverID;
        this.ID=ID;
        this.sendTime=sendTime;
        this.seenString=seenString;
        this.isDeletedForString=isDeletedForString;
        this.replyTo=replyTo;
        this.forwardFrom=forwardFrom;
        this.isDeleted=isDeleted;
        this.hasBeenForwarded=hasBeenForwarded;
        this.filePath=filePath;
    }
}
class AppUser{
    String name="Unnamed";
    String username="";
    String password="";
    String securityQuestion="",securityHint="",securityAnswer="";
    boolean isBusiness;
    String filePath="C:\\OOP File DataBase\\neutralProfile.jpg";
    String bio="Hi There!";
    ArrayList<AppUser> followers=new ArrayList<>();
    String followersString="",followingsString="",chatsString="",pinnedChatsString="",postsString="",
    followRequestsString="",blockedUsersString="";
    ArrayList<AppUser> followings=new ArrayList<>(),followRequests=new ArrayList<>(),blockedUsers=new ArrayList<>();
    boolean twoStep=false,isPrivate=false;
    String emailAddress;
    ArrayList<AppChat> chats=new ArrayList<>();
    int ID;
    boolean isDeactivated=false,isDark=false;
    ArrayList<AppPost> posts=new ArrayList<>();
    LocalDateTime lastSeen;
    ArrayList<AppChat> pinnedChats=new ArrayList<>();
    //ArrayList<AppSeen> profileSeens=new ArrayList<>();
    ArrayList<AppStory> stories=new ArrayList<>();
    AppUser(String username, String password, String securityQuestion, String securityHint, String securityAnswer,
                   String emailAddress,int ID) {
        this.username=username;
        this.password=password;
        this.securityQuestion=securityQuestion;
        this.securityHint=securityHint;
        this.securityAnswer=securityAnswer;
        this.emailAddress=emailAddress;
        this.ID=ID;
    }
    AppUser(String name,String username, String password, String securityQuestion, String securityHint, String securityAnswer,
            boolean isBusiness,String filePath,String bio,String followersString,String followingsString,String chatsString,
            String pinnedChatsString,String postsString,String followRequestsString,String blockedUsersString,
            boolean twoStep,boolean isPrivate,String emailAddress,int userID,boolean isDeactivated,boolean isDark,
            LocalDateTime lastSeen) {
        this.name=name;
        this.username=username;
        this.password=password;
        this.securityQuestion=securityQuestion;
        this.securityHint=securityHint;
        this.securityAnswer=securityAnswer;
        this.isBusiness=isBusiness;
        this.filePath=filePath;
        this.bio=bio;
        this.followersString=followersString;
        this.followingsString=followingsString;
        this.chatsString=chatsString;
        this.pinnedChatsString=pinnedChatsString;
        this.postsString=postsString;
        this.followRequestsString=followRequestsString;
        this.blockedUsersString=blockedUsersString;
        this.twoStep=twoStep;
        this.isPrivate=isPrivate;
        this.emailAddress=emailAddress;
        this.ID=userID;
        this.isDeactivated=isDeactivated;
        this.isDark=isDark;
        this.lastSeen=lastSeen;
    }
    public void follow(AppUser newUser){
        if(newUser.isPrivate){
            newUser.followRequests.add(this);
            newUser.followRequestsString+=ID+"_";
        }
        else{
            followings.add(newUser);
            newUser.followers.add(this);
            followingsString+=newUser.ID+"_";
            newUser.followersString+=ID+"_";
        }
    }
    public void acceptFollow(AppUser newUser){
        newUser.followings.add(this);
        followers.add(newUser);
        newUser.followingsString+=ID+"_";
        followersString+=newUser.ID+"_";
    }
    public void unfollow(AppUser newUser){
        if(newUser.followRequests.contains(this)){
            newUser.followRequests.remove(this);
            String s="";
            for(AppUser i:newUser.followRequests){
                s+=i.ID+"_";
            }
            newUser.followersString=s;
        }
        else{
            followings.remove(newUser);
            newUser.followers.remove(this);
            String s1="",s2="";
            for(AppUser i:followings){
                s1+=i.ID+"_";
            }
            for(AppUser i:newUser.followers){
                s2+=i.ID+"_";
            }
            followingsString=s1;
            newUser.followersString=s2;
        }
    }
    @Override
    public boolean equals(Object o){
        if(o!=null){
            if(o instanceof AppUser){
                AppUser newUser=(AppUser) o;
                if(newUser.ID==ID){
                    return true;
                }
            }
        }
        return false;
    }
}
class AppGroup{
    String name="";
    String groupName="";
    AppUser admin;
    int adminID;
    ArrayList<AppUser> members=new ArrayList<>(),bannedUsers=new ArrayList<>();
    String filePath="C:\\OOP File DataBase\\neutralGroup.jpg";
    String bio="",bannedString="",membersString="";
    int ID;
    boolean isDeleted=false;
    AppGroup(AppUser Admin,String name,String sID,ArrayList<AppUser> startingMembers,int id,String bio){
        admin=Admin;
        this.name=name;
        groupName=sID;
        members=startingMembers;
        ID=id;
        this.bio=bio;
    }
    AppGroup(String name,String sID,int adminID,String membersString,String bannedString,String filePath,String bio,int id,
             boolean isDeleted){
        this.name=name;
        this.groupName=sID;
        this.adminID=adminID;
        this.membersString=membersString;
        this.bannedString=bannedString;
        this.filePath=filePath;
        this.bio=bio;
        this.ID=id;
        this.isDeleted=isDeleted;
    }
    @Override
    public boolean equals(Object o){
        if(o!=null){
            if(o instanceof AppGroup){
                AppGroup newGroup=(AppGroup) o;
                if(newGroup.ID==ID){
                    return true;
                }
            }
        }
        return false;
    }
}
class AppStory{
    AppUser sender;
    int senderID,ID;
    boolean isDeleted=false;
    String filePath="",caption="",seensString="";
    LocalDateTime sendTime;
    ArrayList<AppSeen> seens=new ArrayList<>();
    ArrayList<AppUser> seenUsers=new ArrayList<>();
    ArrayList<LocalDateTime> seenTimes=new ArrayList<>();
    AppStory(AppUser sendER,String path,String text,int storyID){
        sendTime=LocalDateTime.now();
        sender=sendER;
        senderID=sendER.ID;
        filePath=path;
        caption=text;
        ID=storyID;
    }
    AppStory(int senderID,int ID,boolean isDeleted,String filePath,String caption,LocalDateTime sendTime,String seensString){
        this.senderID=senderID;
        this.ID=ID;
        this.isDeleted=isDeleted;
        this.filePath=filePath;
        this.caption=caption;
        this.sendTime=sendTime;
        this.seensString=seensString;
    }
    public static boolean delete(AppStory story, boolean flag){
        if(flag){
            story.isDeleted=true;
        }
        else{
            long hours=ChronoUnit.HOURS.between(story.sendTime,LocalDateTime.now());
            if(24<=hours){
                story.isDeleted=true;
            }
        }
        return story.isDeleted;
    }
}
class AppChat{
    String name="";
    String filePath="";
    ArrayList<AppMessage> messages=new ArrayList<>();
    int chatID,ID;
    boolean isDeleted=false;
    String messagesString="";
    @Override
    public boolean equals(Object o){
        if(o!=null){
            if(o instanceof AppChat){
                AppChat newChat=(AppChat) o;
                if(newChat.chatID==chatID){
                    return true;
                }
            }
        }
        return false;
    }
    AppChat(){}
    AppChat(String name,String filePath,String messagesString,int chatID,int ID,boolean isDeleted){
        this.name=name;
        this.filePath=filePath;
        this.messagesString=messagesString;
        this.chatID=chatID;
        this.ID=ID;
        this.isDeleted=isDeleted;
    }
}
class AppPost{
    boolean isDeleted=false;
    AppUser sender;
    int senderID,postID;
    String caption="";
    String filePath="";
    ArrayList<AppSeen> likes=new ArrayList<>();
    ArrayList<AppUser> likesUsers=new ArrayList<>();
    ArrayList<LocalDateTime> likesTimes=new ArrayList<>();
    ArrayList<AppComment> comments=new ArrayList<>();
    ArrayList<AppSeen> seens=new ArrayList<>();
    ArrayList<AppUser> seenUsers=new ArrayList<>();
    ArrayList<LocalDateTime> seenTimes=new ArrayList<>();
    String likesString="",commentsString="",seensString="";
    AppPost(boolean isDeleted,int senderID,int postID,String caption,String filePath,String likesString,String commentsString,
            String seensString){
        this.isDeleted=isDeleted;
        this.senderID=senderID;
        this.postID=postID;
        this.caption=caption;
        this.filePath=filePath;
        this.likesString=likesString;
        this.commentsString=commentsString;
        this.seensString=seensString;
    }
    AppPost(AppUser sendEr,String captionString,String path,int posID){
        sender=sendEr;
        senderID=sendEr.ID;
        caption=captionString;
        if(path.length()!=0){
            filePath=path;
        }
        postID=posID;
    }
}
class AppComment{
    AppUser sender;
    String text="";
    int senderID,replyTo,ID;
    boolean isDeleted=false;
    LocalDateTime sendTime;
    ArrayList<AppUser> upVotes=new ArrayList<>();
    ArrayList<AppUser> downVotes=new ArrayList<>();
    String upVotesString="",downVotesString="";
    AppComment(AppUser sendER,String text1,int ID,int ReplyTo){
        sender=sendER;
        senderID=sendER.ID;
        text=text1;
        this.ID=ID;
        replyTo=ReplyTo;
        sendTime=LocalDateTime.now();
    }
    AppComment(String text,int senderID,int replyTo,int ID,boolean isDeleted,LocalDateTime sendTime,String upVotesString,
               String downVotesString){
        this.text=text;
        this.senderID=senderID;
        this.replyTo=replyTo;
        this.ID=ID;
        this.isDeleted=isDeleted;
        this.sendTime=sendTime;
        this.upVotesString=upVotesString;
        this.downVotesString=downVotesString;
    }
}
class AppSeen{
    AppUser user;
    int userID;
    LocalDateTime time;

    AppSeen(AppUser currUser, LocalDateTime now) {
        user=currUser;
        time=now;
        userID= currUser.ID;
    }
    AppSeen(int currUserID, LocalDateTime now) {
        time=now;
        userID=currUserID;
        for(AppUser i:DataBaseStuff.appUsers){
            if(currUserID==i.ID){
                user=i;
            }
        }
    }
    @Override
    public boolean equals(Object o){
        if(o!=null){
            if(o instanceof AppSeen){
                AppSeen newSeen=(AppSeen) o;
                if(newSeen.user.ID==this.user.ID){
                    return true;
                }
            }
        }
        return false;
    }
}
class AppEmail {
    static String accountUsername;
    static String recipient;
    static String forgottenPassword;
    static String sentCode;
    AppEmail(String username,String address,String forgotten){
        recipient=address;
        forgottenPassword=forgotten;
        accountUsername=username;
    }
    public static void sendMail(int flag) throws Exception {
        Stage stage=Alerts.Alert("Please Wait!","Preparing To Send The Code To Your Email!");
        stage.show();
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        String myEmail="oop.project.sut14002@gmail.com";
        String password="zefmciidekyezzwc";
        Session session=Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myEmail, password);
            }
        });
        Message message = prepareMessage(session, myEmail,flag);
        Transport.send(message);
        stage=Alerts.Alert("Successful Operation!","The Code Was Sent to Your Email!");
        stage.show();
    }

    public static Message prepareMessage(Session session,String myEmail,int flag) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            if (flag==1) {
                message.setSubject("Confirmation Email");
                sentCode= RandomStringUtils.randomAlphanumeric(6);
                message.setText("Hey there,\n" +
                        "Account with username: "+accountUsername+" has 2-step verification enabled, so you'll have to enter " +
                        "the proceeding string into the app to successfully login:\n\n " + sentCode +
                        "\n\nHave a nice day!");
            }
            else if(flag==2){
                message.setSubject("Password Recovery");
                message.setText("Password to the account with username: "+accountUsername+" is:\n\n " + forgottenPassword + "\n\nHave a nice day!");
            }
            return message;
        } catch (Exception e) {
            Logger.getLogger(AppEmail.class.getName());
        }
        return null;
    }
}