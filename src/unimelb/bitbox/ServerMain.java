package unimelb.bitbox;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import unimelb.bitbox.util.Configuration;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.FileSystemManager;
import unimelb.bitbox.util.FileSystemObserver;
import unimelb.bitbox.util.FileSystemManager.FileSystemEvent;

public class ServerMain implements FileSystemObserver {
	private static Logger log = Logger.getLogger(ServerMain.class.getName());
	protected static FileSystemManager fileSystemManager;
	private Peer peer = new Peer() ;


	
	
	public ServerMain() throws NumberFormatException, IOException, NoSuchAlgorithmException {
		fileSystemManager = new FileSystemManager(Configuration.getConfigurationValue("path"),this);

		peer.start();

	}
	
	
	@Override
	public void processFileSystemEvent(FileSystemEvent fileSystemEvent) {
		Document file_descriptor = new Document();

		if(fileSystemEvent.event.equals(FileSystemManager.EVENT.FILE_CREATE)){
			Document file_create = new Document();
			file_create.append("command", "FILE_CREATE_REQUEST");
					file_descriptor.append("md5",fileSystemEvent.fileDescriptor.md5);
					file_descriptor.append("lastModified",fileSystemEvent.fileDescriptor.lastModified);
					file_descriptor.append("fileSizes",fileSystemEvent.fileDescriptor.fileSize);
			file_create.append("fileDescriptor",file_descriptor.toJson());
			file_create.append("pathName",fileSystemEvent.pathName);
			String message = file_create.toJson();
			peer.sentToOtherPeers(message);
		}

	}
	
}
	