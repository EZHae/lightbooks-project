package com.itwill.lightbooks.web;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate; //통계 차트때문에 추가
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.lightbooks.domain.CoinPayment;
import com.itwill.lightbooks.domain.CoinPaymentWaiting;
import com.itwill.lightbooks.domain.MileagePayment;
import com.itwill.lightbooks.domain.Notification;
import com.itwill.lightbooks.domain.TicketPayment;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.domain.UserWallet;
import com.itwill.lightbooks.dto.LikedNovelBookmarkDto;
import com.itwill.lightbooks.dto.NotificationFragmentDto;
import com.itwill.lightbooks.dto.PaymentRequestDto;
import com.itwill.lightbooks.dto.PurchasedNovelBookmarkDto;
import com.itwill.lightbooks.dto.RecentlyWatchedEpisodeDto;
import com.itwill.lightbooks.dto.TicketReadDto;
import com.itwill.lightbooks.dto.UserSignUpDto;
import com.itwill.lightbooks.dto.UserUpdatePasswordDto;
import com.itwill.lightbooks.dto.UserUpdateProfileDto;
import com.itwill.lightbooks.service.BookmarkService;
import com.itwill.lightbooks.service.OrderService;
import com.itwill.lightbooks.service.TicketService;
import com.itwill.lightbooks.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
	
	private final UserService userService;
	private final OrderService orderService;
	private final TicketService ticketService;
	private final BookmarkService bookmarkService;
	private final JdbcTemplate jdbcTemplate;//추가(통계 관련)
    
    @GetMapping("/signup")
    public void signup() {
        log.info("GET signup");
    }
    
    @PostMapping("/signup")
    public String signup(UserSignUpDto dto) {
    	log.info("POST signup");
    	
    	User user = userService.create(dto);
    	log.info("회원가입 성공: {}", user);
    	
    	return "redirect:/";
    }
    
    @PreAuthorize("isAuthenticated() and principal.id == #id")
    @GetMapping("/details")
    public void details(@RequestParam(name = "id") Long id, Model model) {
    	log.info("GET details");
    	
    	User user = userService.searchById(id);
    	
    	model.addAttribute("user", user);
    }
    
    @PostMapping("/updateProfile")
    public String updateProfile(UserUpdateProfileDto dto) {
    	log.info("POST updateProfile");
    	
    	userService.updateProfile(dto);
    	
    	return "redirect:/user/signout";
    }
    
    @PostMapping("/updatePassword")
    public String updatePassword(UserUpdatePasswordDto dto) {
    	
    	userService.updatePassword(dto);
    	
    	return "redirect:/user/signout";
    }
    
    @PostMapping("/delete")
    public String deleteById(@RequestParam(name = "id") Long id) {
    	
    	userService.deleteUser(id);
    	
    	return "redirect:/user/signout";
    }
    
    @PreAuthorize("isAuthenticated() and principal.id == #id")
    @GetMapping("/coinpayment")
    public String coinPayment(@RequestParam(name = "id") Long id) {
    	log.info("coinPayment(id={})", id);
    	
    	return "/user/coin-payment";
    }
    
    @PreAuthorize("isAuthenticated() and principal.id == #id")
    @GetMapping("/mileagepayment")
    public String mileagePayment(@RequestParam(name = "id") Long id) {
    	log.info("mileagePayment(id={})", id);
    	
    	return "/user/mileage-payment";
    }
    
    @PreAuthorize("isAuthenticated() and principal.id == #id")
	@GetMapping("/bookmark")
	public String bookmarkPage(Model model, @RequestParam(name = "id") Long id, @RequestParam(name = "type") String type) {
		log.info("북마크페이지");
		
		model.addAttribute("type", type);
		
		return "user/bookmark";
	}
    
    
	@PostMapping("/bookmark/data")
	@ResponseBody
	public ResponseEntity<Page<?>> bookmark(@RequestBody Map<String, Object> requestBody) {
		log.info("Processing bookmark API request: {}", requestBody);

		Long id = Long.valueOf(requestBody.get("id").toString());
		String type = requestBody.get("type").toString();
		int pageNo = requestBody.containsKey("p") ? Integer.parseInt(requestBody.get("p").toString()) : 0;
		String sortBy = requestBody.containsKey("s") ? requestBody.get("s").toString() : "novel.likeCount";
		String direction = requestBody.containsKey("d") ? requestBody.get("d").toString() : "DESC";

		log.info("Parsed parameters: id={}, type={}, pageNo={}, sortBy={}, direction={}", id, type, pageNo, sortBy,
				direction);

		// 유효성 검사
		if (id == null || id <= 0) {
			log.error("Invalid ID: {}", id);
			return ResponseEntity.badRequest().body(null);
		}
		if (!"liked".equals(type) && !"watched".equals(type) && !"purchased".equals(type)) {
			log.error("Invalid type: {}", type);
			return ResponseEntity.badRequest().body(null);
		}

		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(pageNo, 20, sort);

		// 타입별 데이터 반환
		switch (type) {
		case "liked":
			Page<LikedNovelBookmarkDto> likedNovels = bookmarkService.getLikedNovels(id, pageNo, sort);
			return ResponseEntity.ok(likedNovels);
		case "watched":
			Page<RecentlyWatchedEpisodeDto> recentlyWatched = bookmarkService.getRecentlyWatchedEpisodes(id, pageNo,
					sort);
			return ResponseEntity.ok(recentlyWatched);
		case "purchased":
			Page<PurchasedNovelBookmarkDto> purchasedNovels = bookmarkService.getPurchasedNovels(id, pageNo, sort);
			return ResponseEntity.ok(purchasedNovels);
		default:
			return ResponseEntity.badRequest().body(null);
		}
	}
    
	@PostMapping("/episode/{episodeId}/access")
	@ResponseBody
	public ResponseEntity<Void> updateAccessTime(
	        @RequestParam("userId") Long userId,
	        @PathVariable Long episodeId) {

	    bookmarkService.updateAccessTime(userId, episodeId);
	    return ResponseEntity.ok().build();
	}
	
	@ResponseBody
	@GetMapping("/api/statistics")
	public ResponseEntity<Map<String, List<Object>>> getStatistics( //통계 추가!
	        @RequestParam("type") String type,
	        @RequestParam("userId") Long userId) {

	    try {
	        Map<String, List<Object>> response = new HashMap<>();
	        List<String> labels = new ArrayList<>();
	        List<Integer> values = new ArrayList<>();

	        String query = "";
	        List<Map<String, Object>> rows;

	        if ("genre".equals(type)) { //장르별
	            query = "SELECT g.name AS label, COUNT(b.episode_id) AS value " +
	                    "FROM BOOKMARK b " +
	                    "JOIN EPISODES e ON b.episode_id = e.id " +
	                    "JOIN NOVELS n ON e.novel_id = n.id " +
	                    "JOIN NOVEL_GENRE ng ON n.id = ng.novel_id " +
	                    "JOIN GENRES g ON ng.genre_id = g.id " +
	                    "WHERE b.type = 1 AND b.user_id = ? " +
	                    "GROUP BY g.name";

	            rows = jdbcTemplate.queryForList(query, userId);
	            
	        } else if ("date".equals(type)) {//날짜별
	            query = "SELECT DATE_FORMAT(b.access_time, '%Y-%m-%d') AS label, COUNT(b.episode_id) AS value " +
	                    "FROM BOOKMARK b " +
	                    "JOIN EPISODES e ON b.episode_id = e.id " +
	                    "WHERE b.type = 1 AND b.user_id = ? " +
	                    "GROUP BY label " +
	                    "ORDER BY label";

	            rows = jdbcTemplate.queryForList(query, userId);
	            
	        } else {
	            return ResponseEntity.badRequest().body(null);
	        }

	        for (Map<String, Object> row : rows) {
	            String label = String.valueOf(row.get("label"));
	            labels.add(label);
	            values.add(((Number) row.get("value")).intValue());
	        }

	        List<Object> labelsObject = new ArrayList<>(labels);
	        List<Object> valuesObject = new ArrayList<>(values);

	        response.put("labels", labelsObject);
	        response.put("values", valuesObject);

	        // 누적 회차 수(date타입인 경우)
	        if ("date".equals(type)) {
	            List<Integer> cumulativeValues = new ArrayList<>();
	            int sum = 0;
	            for (int value : values) {
	                sum += value;
	                cumulativeValues.add(sum);
	            }
	            List<Object> cumulativeValuesObject = new ArrayList<>(cumulativeValues);
	            response.put("cumulativeValues", cumulativeValuesObject);
	        }

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        Map<String, List<Object>> errorResponse = new HashMap<>();
	        errorResponse.put("error", Arrays.asList(e.getMessage()));
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}
    
	@PreAuthorize("isAuthenticated() and principal.id == #id")
    @GetMapping("/ticket")
    public void ticket(@RequestParam(name = "id") Long id) {
    	log.info("ticket(id={})", id);
    }

	@PreAuthorize("isAuthenticated() and principal.id == #id")
    @GetMapping("/notification")
    public void notification(@RequestParam(name = "id") Long id) {
    	log.info("notification(id={})", id);
    }    
    
    /* ResponseBody */
    @ResponseBody
    @GetMapping("/checkLoginId")
    public ResponseEntity<Boolean> checkLoginId(@RequestParam(name = "loginId") String loginId) {
    	log.info("check?loginId()");
    	
    	User user = userService.searchByLoginId(loginId);
    	log.info("searchByLoginId(loginId={})", loginId);
    	
    	Boolean result = (user == null) ? false : true;
    	log.info("reuslt={}", result);
    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @GetMapping("/checkNickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam(name = "nickname") String nickname) {
    	log.info("check?nickname()");
    	
    	User user = userService.searchByNickname(nickname);
    	log.info("searchByNickname(nickname={})", nickname);
    	
    	Boolean result = (user == null) ? false : true;
    	log.info("reuslt={}", result);
    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @GetMapping("/checkPhonenumber")
    public ResponseEntity<Boolean> checkPhonenumber(@RequestParam(name = "phonenumber") String phonenumber) {
    	log.info("check?phonenumber()");
    	
    	User user = userService.searchByPhonenumber(phonenumber);
    	log.info("searchByPhonenumber(phonenumber={})", phonenumber);
    	
    	Boolean result = (user == null) ? false : true;
    	log.info("reuslt={}", result);
    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @GetMapping("/checkEmail")
    public ResponseEntity<Boolean> checkEmail(@RequestParam(name = "email") String email) {
    	log.info("check?email()");
    	
    	User user = userService.searchByEmail(email);
    	log.info("searchByEmail(email={})", email);
    	
    	Boolean result = (user == null) ? false : true;
    	log.info("reuslt={}", result);
    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @PostMapping("/checkOldPassword")
    public ResponseEntity<Boolean> checkPassword(@RequestBody UserUpdatePasswordDto dto) {
    	log.info("{}", dto);
    	
    	Boolean result = userService.checkPassword(dto);
    	
    	return ResponseEntity.ok(result);
    }
    
    /*
    @ResponseBody
    @GetMapping("/getUserWallet")
    public ResponseEntity<Long> getUserWallet(@RequestParam(name = "userId") Long userId,
    												@RequestParam(name = "type") String type) {
    	log.info("getUserWallet?userId");
    	log.info("userId={}", userId);
    	
    	UserWallet userWallet = userService.SearchUserWalletByUserId(userId);
    	log.info("userWallet={}", userWallet);
    	
    	if (type.equals("c")) {
    		return ResponseEntity.ok(userWallet.getCoin());
    	} else {
    		return ResponseEntity.ok(userWallet.getMileage());
    	}
    }
    */
    
    @ResponseBody
    @GetMapping("/getUserWallets")
    public ResponseEntity<UserWallet> getUserWallets(@RequestParam(name = "userId") Long userId) {
    	log.info("getUserWallet?userId");
    	log.info("userId={}", userId);
    	
    	UserWallet userWallet = userService.SearchUserWalletByUserId(userId);
    	log.info("잉userWallet={}", userWallet);
    	
    	return ResponseEntity.ok(userWallet);
    }
    
    // Servlet 또는 Controller에서 세션에서 errorMessage를 제거하는 코드
    @ResponseBody
    @PostMapping("/removeSession")
    public ResponseEntity<String> clearErrorMessage(HttpSession session) {
        session.removeAttribute("errorMessage");
        return ResponseEntity.ok("ok");
    }
    
    @ResponseBody
    @GetMapping("/coinpayment/read")
    public ResponseEntity<Page<CoinPayment>> readCoinPayment(@RequestParam(name = "userId") Long userId,
    	    @RequestParam(name = "page", defaultValue = "0") int page,
    	    @RequestParam(name = "size", defaultValue = "5") int size,
    	    @RequestParam(name = "type", defaultValue = "0") int type) {
    	
    	Page<CoinPayment> result = orderService.readCoinPaymentByUserId(userId, page, size, type);
    	log.info("이거보세요.{}", result);
    	
        // 페이징 정보와 데이터 함께 전달
//        Map<String, Object> response = new HashMap<>();
//        response.put("data", result.getContent());
//        response.put("totalItems", result.getTotalElements());
//        response.put("totalPages", result.getTotalPages());
//        response.put("currentPage", result.getNumber());
    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @GetMapping("/coinpaymentwaiting/read")
    public ResponseEntity<Page<CoinPaymentWaiting>> readCoinPaymentWaiting(@RequestParam(name = "userId") Long userId,
    	    @RequestParam(name = "page", defaultValue = "0") int page,
    	    @RequestParam(name = "size", defaultValue = "5") int size,
    	    @RequestParam(name = "type", defaultValue = "0") int type) {
    	
    	Page<CoinPaymentWaiting> result = orderService.readCoinPaymentWaitingByUserId(userId, page, size, type);
    	log.info("waiting.{}", result);
    	
        // 페이징 정보와 데이터 함께 전달
//        Map<String, Object> response = new HashMap<>();
//        response.put("data", result.getContent());
//        response.put("totalItems", result.getTotalElements());
//        response.put("totalPages", result.getTotalPages());
//        response.put("currentPage", result.getNumber());
    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @PostMapping("/coinpayment/reApp")
    public ResponseEntity<String> coinPaymentReApp(@RequestBody PaymentRequestDto dto) {
    	log.info("coinPaymentReApp()");
    	User user = userService.searchById(dto.getUserId());
    	int type = dto.getType();
    	
    	CoinPayment coinpayment = CoinPayment.builder().userId(dto.getUserId()).coin(dto.getCoin()).cash(dto.getCash()).type(dto.getType()).build();
    	CoinPaymentWaiting coinpaymentWaiting = CoinPaymentWaiting.builder().user(user).coin(dto.getCoin()).cash(dto.getCash()).type(dto.getType()).con(0).build();
    	
    	orderService.saveCoinPayment(coinpayment);
    	orderService.saveCoinPaymentWaiting(coinpaymentWaiting);
    	
    	return ResponseEntity.ok("/");
    }

    @ResponseBody
    @GetMapping("/mileagepayment/read")
    public ResponseEntity<Page<MileagePayment>> readMileagePayment(@RequestParam(name = "userId") Long userId,
    	    @RequestParam(name = "page", defaultValue = "0") int page,
    	    @RequestParam(name = "size", defaultValue = "5") int size,
    	    @RequestParam(name = "type", defaultValue = "0") int type) {
    	
    	Page<MileagePayment> result = userService.readMileagePaymentByUserId(userId, page, size, type);

    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @PostMapping("/exchange/ticket")
    public ResponseEntity<String> mileagepaymentApp(@RequestBody PaymentRequestDto dto) {
    	log.info("mileagepaymentApp()");
    	
    	userService.saveMileagePaymentWithTicket(dto);
    	
    	return ResponseEntity.ok("/");
    }
    
    @ResponseBody
    @GetMapping("/ticket/read")
    public ResponseEntity<Page<TicketReadDto>> readTicket(@RequestParam(name = "userId") Long userId,
		    @RequestParam(name = "page", defaultValue = "0") int page,
		    @RequestParam(name = "size", defaultValue = "5") int size) {
    	log.info("readTicket()");
		    	
    	Page<TicketReadDto> result = ticketService.readTicketByUserId(userId, page, size, size);
    	
    	return ResponseEntity.ok(result);
	}
    
    @ResponseBody
    @GetMapping("/ticketpayment/read")
    public ResponseEntity<Page<TicketPayment>> readTicetPayment(@RequestParam(name = "userId") Long userId,
		    @RequestParam(name = "page", defaultValue = "0") int page,
		    @RequestParam(name = "size", defaultValue = "5") int size) {
    	log.info("readTicetPayment()");
    	
    	Page<TicketPayment> result = ticketService.readTicketPaymenByUserId(userId, page, size, size);
    	
    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @GetMapping("/notification/fragment")
    public ResponseEntity<NotificationFragmentDto> readNotificationFragmentDto(@RequestParam(name = "userId") Long userId) {
    	log.info("readNotificationFragmentDto()");
    	
    	NotificationFragmentDto dto = userService.countNoReadNotificationAndFindNoReadNotification(userId, 0);
    	
    	return ResponseEntity.ok(dto);
    }
    
    @ResponseBody
    @PutMapping("/notification/read/all")
    public ResponseEntity<?> allReadNotification(@RequestParam(name = "userId") Long userId) {
    	
    	userService.updateNotificationsAllReadByUserId(userId);
    	
    	return ResponseEntity.ok(null);
    }
    
    @ResponseBody
    @GetMapping("/notification/get")
    public ResponseEntity<List<Notification>> getNotification(@RequestParam(name = "userId") Long userId) {
    	
    	List<Notification> notifications = userService.readNotificationByUserId(userId);
    	
    	return ResponseEntity.ok(notifications);
    }
    
    @ResponseBody
    @DeleteMapping("/notification/delete")
    public ResponseEntity<?> deleteNotificationById(@RequestParam(name = "id") Long id) {
    	
    	userService.deleteNotificationById(id);
    	
    	return ResponseEntity.ok(null);
    }
    
    @ResponseBody
    @DeleteMapping("/notification/delete/all")
    public ResponseEntity<?> deleteNotificationByUserId(@RequestParam(name = "userId") Long userId) {
    	
    	userService.deleteNotificationByUserId(userId);
    	
    	return ResponseEntity.ok(null);
    }
    
    @ResponseBody
    @PutMapping("/notification/read")
    public ResponseEntity<?> readNotification(@RequestParam(name = "id") Long id) {
    	log.info("업데이트 시도");
    	
    	userService.updateNotificationReadById(id);
    	
    	return ResponseEntity.ok(null);
    }
    
    @ResponseBody
    @PostMapping("/donation")
    public ResponseEntity<?> donation(@RequestBody PaymentRequestDto dto) {
    	log.info("donation(dto={})", dto);
    	
    	userService.saveCoinPaymentFromDonation(dto);
    	
    	return ResponseEntity.ok(null);
    }
    
    @ResponseBody
    @GetMapping("/donation/ranking")
    public ResponseEntity<List<Object[]>> test(@RequestParam(name = "novelId") Long novelId) {
    	List<Object[]> result = userService.test(novelId);
    	
    	return ResponseEntity.ok(result);
    }
}
