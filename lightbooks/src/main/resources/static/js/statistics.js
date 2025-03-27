document.addEventListener('DOMContentLoaded', function() {
    $(document).ready(function() {
        const statsButton = $("#statsButton");
        const statsModal = $("#statsModal");
        const chartTypeFilter = $("#chartType");
        const chartCanvas = $("#statsChart");
        let currentChart;
      const userId = document.querySelector('input#userId').value;
         console.log("User ID:", userId);

        function initializeChart(chartType, userId) {
            if (currentChart) {
                currentChart.destroy();
            }
            if (!chartCanvas.length) {
                console.error("차트를 렌더링할 캔버스가 없음!");
            }

            $.ajax({
                url: "/user/api/statistics",
                method: "GET",
                data: { type: chartType, userId: userId },
                dataType: "json",
                success: function(data) {
                    console.log("요청 데이터:", { type: chartType, userId: userId });
                    console.log("AJAX 응답 데이터:", data);

                    if (!data || !data.labels || !data.values || data.labels.length !== data.values.length) {
                        console.error("잘못된 통계 데이터:", data);
                        $("#chart-error").text("통계 데이터를 불러오는 데 실패했습니다.");
                        return;
                    }

                    console.log("차트 타입:", chartType, "userId", userId);

                    if (!data.values.every(value => typeof value === 'number')) {
                        console.error("숫자 데이터가 아닌 값이 포함됨:", data.values);
                        $("#chart-error").text("통계 데이터를 불러오는 데 실패했습니다.");
                        return;
                    }

                    let chartConfig;

                    if (chartType === "genre") {
                        chartConfig = {
                            type: "pie", //파이차트!
                            data: {
                                labels: data.labels, //각 장르명을 나타내는 배열!!!
                                datasets: [{
                                    label: "읽은 회차 수",
                                    data: data.values,  //각 장르별 회차 수 데이터 배열!!!
                                    backgroundColor: [
                                        'rgba(255, 99, 132, 0.5)',
                                        'rgba(54, 162, 235, 0.5)',
                                        'rgba(255, 206, 86, 0.5)',
                                        'rgba(75, 192, 192, 0.5)',
                                        'rgba(153, 102, 255, 0.5)',
                                        'rgba(255, 159, 64, 0.5)'
                                    ]
                                }]
                            },
                            options: {
                        responsive: false, //반응형 크기 조절 비활성화
                        maintainAspectRatio: false, //가로 세로 비율 유지 비활성화
                        title: {
                           display: true,
                           text: '장르별 회차 통계'
                        },
                        plugins: {
                                legend: {
                                    position: 'right', //라벨을 차트 오른쪽에 세로로 배치
                                    labels: {
                                        boxWidth: 25, //색상 상자의 너비 설정
                                        padding: 15, //각 라벨 사이 간격
                                        font: {
                                            size: 13 //라벨 글씨 크기
                                        }
                                    }
                                }
                            }

                            }
                        };
                    } else if (chartType === "date") {
                        chartConfig = {
                            type: "bar", //막대차트!

                            data: {
                                labels: data.labels, //날짜 라벨 배열!!!
                                datasets: [{
                                    label: "읽은 회차 수",
                                    data: data.values, //날짜별 회차 수 데이터 배열!!!

                                    backgroundColor: "rgba(54, 162, 235, 0.5)"
                                }]
                            },
                            options: {
                                responsive: false,
								maintainAspectRatio: false, // 가로 세로 비율 유지 비활성화
                                title: {
                                    display: true,
                                    text: "날짜별 회차 통계"
                                },
                                scales: {
                                    y: {
                                        beginAtZero: true,
                                        ticks: {
                                            stepSize: 1
                                        }
                                    }
                                }
                            }
                        };
                    }

                    currentChart = new Chart(chartCanvas, chartConfig);
                },
                error: function(xhr, status, error) {
                    console.error("통계 데이터 요청 실패:", status, error);
                    $("#chart-error").text("통계 데이터를 불러오는 데 실패했습니다.");
                }
            });
        }

        // userId 변수 정의
        

        if (userId) {
            initializeChart("genre", userId);
        } else {
            console.error("userId를 찾을 수 없습니다.");
        }

        chartTypeFilter.change(function() {
            if (userId) {
                initializeChart($(this).val(), userId);
            } else {
                console.error("userId를 찾을 수 없습니다.");
            }
        });

        statsButton.click(function() {
            if (userId) {
                statsModal.modal("show");
                initializeChart(chartTypeFilter.val(), userId);
            } else {
                console.error("userId를 찾을 수 없습니다.");
            }
        });
    });
   
   // =======모달 닫기 버튼 ===========
       const statsModal = document.getElementById('statsModal');
       const closeButton = statsModal.querySelector('.btn-close'); 
       const footerCloseButton = statsModal.querySelector('.btn-danger');

       closeButton.addEventListener('click', function () {
           $(statsModal).modal('hide');
       });

       footerCloseButton.addEventListener('click', function () {
           $(statsModal).modal('hide');
       });
   
   // ============ 모달 ============
      // 모달창 nav 뒤로 밀어버리는 코드
      const navbar = document.querySelector('.navbar');
      const modalEl = document.getElementById("statsModal");

      // 모달 열릴 때 navbar 위치 변경
      modalEl.addEventListener('show.bs.modal', () => {
        navbar.style.position = 'static';     // navbar를 static으로 만들어서 모달 앞에 겹치지 않도록
        navbar.style.zIndex = '1';            // z-index 낮추기
      });

      // 모달 닫힐 때 navbar 복구
      modalEl.addEventListener('hidden.bs.modal', () => {
        navbar.style.position = 'sticky';    // 모달 닫으면 다시 sticky로 복원
        navbar.style.zIndex = '1005';         // z-index 복원
      });
      // 스크롤 이벤트 리스너 추가
      window.addEventListener('scroll', () => {
        // 스크롤이 10px 이상 내렸다면
        if (window.scrollY > 10) {
          navbar.style.boxShadow = '0 4px 8px rgba(0, 0, 0, 0.05)'; // 그림자 효과 추가
        } else {
          navbar.style.boxShadow = 'none'; // 스크롤이 맨 위로 올라오면 그림자 없앰
        }
      });
});