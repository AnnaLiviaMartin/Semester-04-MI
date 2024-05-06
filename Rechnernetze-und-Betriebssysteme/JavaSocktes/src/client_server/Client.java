package client_server;

import java.io.*;
import java.net.Socket;

public class Client
{

	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;

	public Client()
	{
		try
		{
			socket = new Socket("localhost", 27999);
			System.out.println("Client connected end with \"END\"");

			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void sendText(final String text)
	{
		try
		{
			System.out.println("Sende an Server: " + text);
			writer.write(text + "\n");
			writer.flush();

			final String line = reader.readLine();
			System.out.println("Vom Server empfangen: " + line);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void close()
	{
		try
		{
			socket.shutdownOutput();
			socket.close();

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException
	{
		Client client = new Client();

		final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (true)
		{
			final String line = reader.readLine();
			if (line.equals("END")) {
				break;
			}
			client.sendText(line);
		}
		client.close();
	}
}
