package twitterterminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.DirectMessage;

public class KSFTwit 
{

	private final static String CONSUMER_KEY = "KihLmaL3Y3RrFklVrDg1UA";
	private final static String CONSUMER_KEY_SECRET = "Gl8FUbUVvqUS8eiY1K4TNcCpUAZhMmYCO36gx2EVBo";
	
	public void start() throws TwitterException, IOException
	{
		
		/*Using TwitterFactory to authenticate the session.*/
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
		RequestToken requestToken = twitter.getOAuthRequestToken();
		String authURL = requestToken.getAuthorizationURL();
		
		/*Displaying URL where the user may obtain the pin number needed
		to authorize the application. Copying it directly into the clipboard
		because the windows command prompt does not allow you to highlight
		and copy.*/
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection strSel = new StringSelection(authURL);
		clipboard.setContents(strSel, null);
		
		System.out.println("Authorization URL: \n" + requestToken.getAuthorizationURL());
		System.out.println("The URL has been automatically copied to your clipboard " +
		"for your convenience. Please visit the page in order to retrieve your PIN.");
		
		AccessToken accessToken = null;
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


		while(null == accessToken)
		{
			try
			{
				System.out.print("Input PIN here: ");
				String pin = br.readLine();
				
				accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				twitter.setOAuthAccessToken(accessToken);
			} 
			
			catch (TwitterException te)
			{
				System.out.println("Failed to get access token, caused by: " + te.getMessage());
				
				System.out.println("Retry input PIN");
			}
			
		}
	
		instructions();
		
		Scanner scan = new Scanner(System.in);
		int request = scan.nextInt();
		
		/*User input determines what functions they access.*/
		while(request != 5)
		{
			
			switch(request)
			{
				case 1:
					
				    try 
				    {
				    	
				    	System.out.println("");
				    	
				    	System.out.println("Please enter your tweet. Remember to keep it" +
				    	" to 140 characters or less!");

				    	/*Store the tweet as a string, then update using the updateStatus method.*/
				    	String tweet = br.readLine();
				        Status status = twitter.updateStatus(tweet);
				        System.out.println("Status updated to [" + status.getText() + "].");
				        
				    }
				    catch (TwitterException te)
				    {
				        System.out.println("Error: "+ te.getMessage()); 
				    }
					break;
					
				case 2:
					
					try
					{
						System.out.println("");
						
						/*This will display the twenty most recet tweets*/
						ResponseList<Status> list = twitter.getHomeTimeline();
						for (Status each : list) 
						{
				 
							System.out.println("Sent by: @" + each.getUser().getScreenName()
				            + " - " + each.getUser().getName() + "\n" + each.getText()
				            + "\n");
				        
						}
	
					}
					catch(TwitterException te)
					{
						
						System.out.println("Error: "+ te.getMessage()); 
						
					}
					
					break;
					
				case 3:	
					
					
					try
					{
						
						System.out.println("");
						
						/*Stores the recipient's username and the message to be sent as strings.
						Completes the process using the sendDirectMessage method.*/
						System.out.print("Who would you like to send a direct message to? ");
						String recipientId = br.readLine();
						
						System.out.print("What would you like to tell them? ");
						String directMessage = br.readLine();
						
				    	DirectMessage message = twitter.sendDirectMessage(recipientId, directMessage);
				    	System.out.println("Sent: " + message.getText() + " to @" + 
				    	message.getRecipientScreenName());
						
					}
					catch(TwitterException te)
					{
						
						System.out.println("Error: "+ te.getMessage()); 
						
					}
					
					break;
				
				case 4:
					
					try 
					{
						
						/*Displays the user's received direct messages.*/
						System.out.println("");
			            Paging paging = new Paging(1);
			            List<DirectMessage> messages;
			            
			            do {
			            	
			                messages = twitter.getDirectMessages(paging);
			                
			                for (DirectMessage message : messages) 
			                {
			                	
			                    System.out.println("From: @" + message.getSenderScreenName() + " id:" + message.getId() + " - "
			                            + message.getText());
			                    
			                }
			                
			                paging.setPage(paging.getPage() + 1);
			                
			            } while (messages.size() > 0 && paging.getPage() < 10);
			            
					}
			        catch(TwitterException te)
					{
							
						System.out.println("Error: "+ te.getMessage()); 
							
					}
					
					break;
				
				/*Default case in order to handle inputs not in the 1 through 4 range.*/	
				default:
					
					System.out.println("Error: Request could not be proccessed.");
					instructions();
					break;
					
			}
				
			System.out.print("?: ");
			request = scan.nextInt();
			
		}
	
		System.out.print("End of run.\n");
		
	}
	
	/*Simple menu with instructions for the user.*/
	public void instructions()
	{
	
		System.out.print("Enter request\n 1- Write a new tweet.\n 2- Display the twenty " +
				"most recent tweets.\n 3- Send a direct message.\n 4- Read your direct messages.\n" +
				" 5- End of run.\n ?: ");
		
	}
	
	public static void main(String[] args) throws Exception
	{
		new KSFTwit().start();
	}
	
}