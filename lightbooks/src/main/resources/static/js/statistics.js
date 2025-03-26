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
                    console.log("차트 구성 데이터:", chartConfig);

                    if (chartType === "genre") {
                        chartConfig = {
                            type: "pie",
                            data: {
                                labels: data.labels,
                                datasets: [{
                                    label: "회차 수",
                                    data: data.values,
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
								responsive: false, // 반응형 크기 조절 비활성화
								            maintainAspectRatio: false, // 가로 세로 비율 유지 비활성화
								            width: 200, // 원하는 너비 설정
								            height: 200, // 원하는 높이 설정
								            title: {
								                display: true,
								                text: '장르별 회차 통계'
								            }
                            }
                        };
                    } else if (chartType === "date") {
                        chartConfig = {
                            type: "bar",
                            data: {
                                labels: data.labels,
                                datasets: [{
                                    label: "회차 수",
                                    data: data.values,
                                    backgroundColor: "rgba(54, 162, 235, 0.5)"
                                }]
                            },
                            options: {
                                responsive: true,
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
});