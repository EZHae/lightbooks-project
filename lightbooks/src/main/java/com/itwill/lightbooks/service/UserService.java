package com.itwill.lightbooks.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.CoinPayment;
import com.itwill.lightbooks.domain.MileagePayment;
import com.itwill.lightbooks.domain.Notification;
import com.itwill.lightbooks.domain.Ticket;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.domain.UserWallet;
import com.itwill.lightbooks.dto.NotificationFragmentDto;
import com.itwill.lightbooks.dto.PaymentRequestDto;
import com.itwill.lightbooks.dto.UserSignUpDto;
import com.itwill.lightbooks.dto.UserUpdateImgSrcDto;
import com.itwill.lightbooks.dto.UserUpdatePasswordDto;
import com.itwill.lightbooks.dto.UserUpdateProfileDto;
import com.itwill.lightbooks.repository.coinpayment.CoinPaymentRepository;
import com.itwill.lightbooks.repository.mileagepayment.MileagePaymentRepository;
import com.itwill.lightbooks.repository.notification.NotificationRepository;
import com.itwill.lightbooks.repository.novel.NovelRepository;
import com.itwill.lightbooks.repository.ticket.TicketRepository;
import com.itwill.lightbooks.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

	private final UserRepository userRepo;
	private final MileagePaymentRepository mileagePaymentRepo;
	private final TicketRepository ticketRepo;
	private final PasswordEncoder passwordEncoder;
	private final NovelRepository novelRepo;
	private final NotificationRepository notifiRepo;
	private final CoinPaymentRepository coinPaymentRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String loginId = username;
		log.info("loginId={}", loginId);
		
		Optional<User> user = userRepo.findByLoginId(loginId);
		log.info("{}", user);
		if (user.isPresent()) {
			User loginUser = user.get();
			
			int todayCheck = loginUser.getTodayCheck();
			if (todayCheck == 0) {
				updateCheck(loginUser);
			}
			
			return user.get();
		} else {
			throw new UsernameNotFoundException(loginId + "과 일치하는 사용자 없음");
		}
	}
	
	public List<User> read() {
		List<User> users = userRepo.findAll();
		return users;
	}
	
	public User searchById(Long id) {
		
		Optional<User> user = userRepo.findById(id);
		if (user.isEmpty()) {
			return null;
		} else {
			return user.get();
		}
	}
	
	public User searchByLoginId(String loginId) {
		
		Optional<User> user = userRepo.findByLoginId(loginId);
		if (user.isEmpty()) {
			return null;
		} else {
			return user.get();
		}
	}
	
	public User searchByNickname(String nickname) {
		
		Optional<User> user = userRepo.findByNickname(nickname);
		if (user.isEmpty()) {
			return null;
		} else {
			return user.get();
		}
	}
	
	public User searchByPhonenumber(String phonenumber) {
		
		Optional<User> user = userRepo.findByPhonenumber(phonenumber);
		if (user.isEmpty()) {
			return null;
		} else {
			return user.get();
		}
	}
	
	public User searchByEmail(String email) {
		
		Optional<User> user = userRepo.findByEmail(email);
		if (user.isEmpty()) {
			return null;
		} else {
			return user.get();
		}
	}
	
	public UserWallet SearchUserWalletByUserId(Long id) {
		
		User user = userRepo.findById(id).orElseThrow();
		UserWallet userWallet = user.getUserWallet();
		
		return userWallet;
	}
	
	@Transactional
	public User create(UserSignUpDto dto) {
		log.info("dto={}", dto);
		
		User savedUser = userRepo.save(dto.toEntity(passwordEncoder));
		
		return savedUser;
	}
	
	public void updateProfile(UserUpdateProfileDto dto) {
		User user = userRepo.findById(dto.getId()).orElseThrow();
		
		user.updateProfile(dto.getUsername(), dto.getNickname(), dto.getPhonenumber(), dto.getEmail());
		
		userRepo.save(user);
	}
	
	public Boolean checkPassword(UserUpdatePasswordDto dto) {
		User user = userRepo.findById(dto.getId()).orElseThrow();
		
		if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
			return true;
		} else {
			return false;
		}
	}
	
	public void updatePassword(UserUpdatePasswordDto dto) {
		User user = userRepo.findById(dto.getId()).orElseThrow();
		
		String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
		user.updatePassword(encodedNewPassword);
		
		userRepo.save(user);
	}
	
	public void updateImgSrc(UserUpdateImgSrcDto dto) {
		User user = userRepo.findById(dto.getId()).orElseThrow();
		
		user.updateImgSrc(dto.getImgSrc());
		
		userRepo.save(user);
	}
	
	public void deleteUser(Long id) {
		userRepo.deleteById(id);
	}
	
	public void updateCheck(User user) {
		user.updateTodayCheck(1);
		userRepo.save(user);
		MileagePayment mileagePayment = MileagePayment.builder().userId(user.getId()).type(0).mileage(100L).descrip("출석 적립").build();
		mileagePaymentRepo.save(mileagePayment);
	}
	
	public Page<MileagePayment> readMileagePaymentByUserId(Long userId, int page, int size, int type) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
		Page<MileagePayment> result = mileagePaymentRepo.findByUserIdAndType(userId, type, pageable);
		
		return result;
	}
	
	public void saveMileagePaymentWithTicket(PaymentRequestDto dto) {
		MileagePayment mileagePayment = MileagePayment.builder()
				.userId(dto.getUserId())
				.type(1)
				.mileage(dto.getMileage())
				.descrip(dto.getDescrip()).build();
		mileagePaymentRepo.save(mileagePayment);
		
		int grade = dto.getGrade();
		
		if (grade == 0) {
			Ticket ticket = Ticket.builder()
				.user(searchById(dto.getUserId()))
				.grade(dto.getGrade()).build();
			ticketRepo.save(ticket);
		} else {
			Ticket ticket = Ticket.builder()
				.user(searchById(dto.getUserId()))
				.novel(novelRepo.findById(dto.getNovelId()).orElseThrow())
				.grade(dto.getGrade()).build();
			ticketRepo.save(ticket);
		}
	}
	
	public NotificationFragmentDto countNoReadNotificationAndFindNoReadNotification(Long userId, int isRead) {
		NotificationFragmentDto dto = new NotificationFragmentDto();
		dto.setCount(notifiRepo.countByUserIdAndIsRead(userId, isRead));
		dto.setNotifications(notifiRepo.findByUserIdAndIsReadOrderByCreatedTimeDesc(userId, isRead));
		
		return dto;
	}
	
	public void updateNotificationsAllReadByUserId(Long userId) {
		notifiRepo.updateNotificationsAllReadByUserId(userId);
	}
	
	public List<Notification> readNotificationByUserId(Long userId) {
		List<Notification> notifications = notifiRepo.findByUserIdOrderByCreatedTimeDesc(userId);
		
		return notifications;
	}
	
	public void deleteNotificationById(Long id) {
		notifiRepo.deleteById(id);
	}
	
	public void deleteNotificationByUserId(Long userId) {
		notifiRepo.deleteByUserId(userId);
	}
	
	public void updateNotificationReadById(Long id) {
		notifiRepo.updateNotificationReadById(id);
	}
	
	public void saveCoinPaymentFromDonation(PaymentRequestDto dto) {
		CoinPayment coinPayment = CoinPayment.builder()
				.userId(dto.getUserId())
				.type(3)
				.novel(novelRepo.findById(dto.getNovelId()).orElseThrow())
				.coin(dto.getCoin()).build();
		coinPaymentRepo.save(coinPayment);
	}
	public List<Object[]> test(Long novelId) {
		List<Object[]> test = coinPaymentRepo.findDonationRankingByNovelId(novelId);	
		test.forEach(dto -> log.info("testdto={}", dto));

		
		return test;
	}
}
