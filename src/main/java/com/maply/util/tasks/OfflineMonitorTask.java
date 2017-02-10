package com.maply.util.tasks;

import com.maply.entity.User;
import com.maply.repository.UserChatRepository;
import com.maply.repository.UserRepository;
import com.maply.service.NotificationService;
import com.maply.service.UserService;
import com.maply.util.Enumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
public class OfflineMonitorTask implements ApplicationListener<ContextRefreshedEvent>
{

	@Autowired
	private UserService userService;


    @Autowired
    private UserRepository userRepository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private UserChatRepository userChatRepository;

	@Scheduled(fixedDelay=60000)
	public void hourlyTrigger()
	{
		List<User> users = userService.geUsersToBeOnline();
		for (User tmp : users) {
		    User user = userRepository.findByIdAndIsPublished(tmp.getId(), true);
			
		    String txtMsg = " timer er gået. Du er nu online igen!";
		    
		    if (user.getLang().equals("en"))
		    	txtMsg = " hours has passed. You're now back online again!";
		    String messages = user.getOfflineHours() + txtMsg;
			
			notificationService.sendIosNotification(user.getAwsArn(), messages,
					Enumeration.NotificationType.GO_ONLINE, "user_id", user.getId(),
					userChatRepository.countByUserAndIsUnread(user, true), user.getId());
            user.setStatus(Enumeration.UserStatus.Live);
            user.setStatusActiveTill(null);
            userRepository.save(user);
		}
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event)
	{

	}

}
