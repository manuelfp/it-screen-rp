package com.devcodes.training.itscreen;

import com.devcodes.training.itscreen.services.CommandService;
import com.devcodes.training.itscreen.utils.ImageTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class ItScreenApplication implements CommandLineRunner {

	private int i = 0;

	public static void main(String[] args) {
		SpringApplication.run(ItScreenApplication.class, args);
	}

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Value("${kafka.topic.dst}")
	private String topicName;

	@Value("${kafka.groupid}")
	private String groupId;

	public void sendMessage(String msg) {
		kafkaTemplate.send(topicName, msg);
	}

	@Value("${file.upload-dir}")
	private String pathImage;

	@Autowired
	private CommandService commandService;

	@KafkaListener(topics = "${kafka.topic.own}")
	public void listenWithHeaders(@Payload String message) {
		System.out.println("Received Message.");
		BufferedImage image = ImageTool.decodeToImage(message);
		String imgType = "png";
		String pathImageComplete = new StringBuilder(pathImage)
				.append("/")
				.append("image-")
				.append(i++)
				.append(".")
				.append(imgType)
				.toString();

		File outPutFile = new File(pathImageComplete);
		try {
			ImageIO.write(image, imgType, outPutFile);
			String response = commandService.doExec(outPutFile.getName());
			sendMessage("response:"+response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run(String... args) throws Exception {
		sendMessage("screen:"+groupId);
	}
}
