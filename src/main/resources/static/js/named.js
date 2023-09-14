(function ($) {
    // Define a Vue instance for search results
    var search_result = new Vue({
        el: '#search-result',
        data: {
            search_result: []
        },
        methods: {
            sortResults: function (category, sortBy) {
                $.get(`/named?category=${category}&sortBy=${sortBy}&page=0&limit=300`, function (response) {
                    search_result.search_result = response.content;
                });
            }
        }
    });

    var currentCategory = ""; // 전역변수

    $("#searchButton1").click(function () {
        var iconUrl = '/markerImages/성시경마커.png';
        var category = '성시경 먹을텐데';
        currentCategory = category;
        performSearch(category, iconUrl);
    });

    $("#searchButton2").click(function () {
        var iconUrl = '/markerImages/이영자마커.png';
        var category = '이영자 맛집';
        currentCategory = category;
        performSearch(category, iconUrl);
    });

    $("#searchButton3").click(function () {
        var iconUrl = '/markerImages/풍자마커.png';
        var category = '또간집';
        currentCategory = category;
        performSearch(category, iconUrl);
    });

    function performSearch(category, icon) {
        $.get(`/named?category=${category}&page=0&limit=300`, function (response) {
            search_result.search_result = response.content;
            initializeMap(category, icon);
        });
    }


    naver.maps.onJSContentLoaded = function () {
        var map = new naver.maps.Map('map', {
            center: new naver.maps.LatLng(35.882682, 127.9647797),
            zoom: 7
        });
    };

    function initializeMap(category, iconUrl) {

        // 서울에 밖에 없음
        if( category === '이영자 맛집') {
            var map = new naver.maps.Map('map', {
                center: getMostFrequentMarkerPosition(),
                zoom: 11
            });
        }

        // 전국적으로 있음
        else if(category === '성시경 먹을텐데' || category === '또간집') {
            var map = new naver.maps.Map('map', {
                center: new naver.maps.LatLng(35.882682, 127.9647797),
                zoom: 7
            });
        }

        var markers = [];

        // Function to create a marker and infowindow for a result
        function createMarker(result) {
            var markerOptions = {
                position: new naver.maps.LatLng(result.mapY, result.mapX),
                map: map,
                title: result.name,
            };

            if (iconUrl) {
                markerOptions.icon = {
                    url: iconUrl,
                    size: new naver.maps.Size(25, 25), // 마커 이미지의 크기를 조절
                    origin: new naver.maps.Point(657, 272), // 이미지의 원점을 설정 가지고 있는 이미지 1440 x 1440
                    anchor: new naver.maps.Point(16, 32) // 이미지의 앵커 지점을 설정
                };
            }

            var marker = new naver.maps.Marker(markerOptions);

            markers.push(marker);

            var contentString = `
                <div class="iw_inner">
                    <p><b>${result.name}</b><br />
                    <b>주소: </b>${result.address}<br />
                    </p>
                </div>`;

            var infowindow = new naver.maps.InfoWindow({
                content: contentString,
                maxWidth: 160,
                backgroundColor: "#eee",
                borderColor: "#2db400",
                borderWidth: 5,
                anchorSize: new naver.maps.Size(30, 30),
                anchorSkew: true,
                anchorColor: "#eee",
                pixelOffset: new naver.maps.Point(20, -20)
            });

            naver.maps.Event.addListener(marker, "click", function () {
                if (infowindow.getMap()) {
                    infowindow.close();
                } else {
                    infowindow.open(map, marker);
                }
            });
        }

        for (var i = 0; i < search_result.search_result.length; i++) {
            createMarker(search_result.search_result[i]);
        }

        $("#sorting-buttons").show();

        $("#nameSortButton").click(function () {
            search_result.sortResults(currentCategory, "이름");
        });

        $("#reviewSortButton").click(function () {
            search_result.sortResults(currentCategory, "리뷰");
        });

        $("#ratingSortButton").click(function () {
            search_result.sortResults(currentCategory, "평점");
        });

    }

    function getMostFrequentMarkerPosition() {
        var searchResults = search_result.search_result;

        // Step 1: 결과를 10개씩 묶기
        var groupedResults = [];
        for (var i = 0; i < searchResults.length; i += 10) {
            var group = searchResults.slice(i, i + 10);
            groupedResults.push(group);
        }

        // Step 2: 각 그룹의 중심점 계산
        var groupCenters = [];
        groupedResults.forEach(function (group) {
            var sumLat = 0;
            var sumLng = 0;
            group.forEach(function (result) {
                sumLat += result.mapY;
                sumLng += result.mapX;
            });
            var centerLat = sumLat / group.length;
            var centerLng = sumLng / group.length;
            groupCenters.push({ lat: centerLat, lng: centerLng });
        });

        // Step 3: 이상치 제거
        var validCenters = removeOutliers(groupCenters);

        // Step 4: 중심점 간의 거리 계산하여 밀집되어 있는 좌표 찾기
        var mostDensePosition = findMostDensePosition(validCenters);

        return mostDensePosition;
    }

// 이상치를 제거하는 함수 (예: 1.5 * IQR 범위로 이상치를 정의)
    function removeOutliers(data) {
        var values = data.map(function (point) {
            return point.lat;
        });
        values.sort(function (a, b) {
            return a - b;
        });

        var q1 = values[Math.floor((values.length / 4))];
        var q3 = values[Math.ceil((values.length * 3 / 4))];
        var iqr = q3 - q1;
        var lowerBound = q1 - 1.5 * iqr;
        var upperBound = q3 + 1.5 * iqr;

        return data.filter(function (point) {
            return point.lat >= lowerBound && point.lat <= upperBound;
        });
    }

// 중심점 간의 거리를 계산하여 가장 밀집된 좌표 찾기
    function findMostDensePosition(centers) {
        var mostDensePosition = null;
        var highestDensity = 0;

        for (var i = 0; i < centers.length; i++) {
            var density = 0;
            for (var j = 0; j < centers.length; j++) {
                if (i !== j) {
                    var distance = calculateDistance(centers[i], centers[j]);
                    density += 1 / distance; // The shorter the distance, the higher the density
                }
            }
            if (density > highestDensity) {
                highestDensity = density;
                mostDensePosition = centers[i];
            }
        }

        return mostDensePosition;
    }

   // 두 지점 사이의 거리를 킬로미터 단위로 반환
    function calculateDistance(point1, point2) {
        var lat1 = point1.lat;
        var lng1 = point1.lng;
        var lat2 = point2.lat;
        var lng2 = point2.lng;

        // Convert latitude and longitude from degrees to radians
        var radLat1 = (Math.PI * lat1) / 180;
        var radLng1 = (Math.PI * lng1) / 180;
        var radLat2 = (Math.PI * lat2) / 180;
        var radLng2 = (Math.PI * lng2) / 180;

        // Earth's radius in kilometers
        var earthRadius = 6371; // You can also use 3958.8 for miles

        // Haversine formula
        var dLat = radLat2 - radLat1;
        var dLng = radLng2 - radLng1;
        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(radLat1) * Math.cos(radLat2) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        var distance = earthRadius * c;

        return distance;
    }

    $(document).ready(function () {
        console.log("init");

        // "지역별 맛집 검색" 링크 클릭 이벤트 처리
        $("#localSectionLink").click(function (event) {
            event.preventDefault(); // 기본 링크 동작을 막습니다.

            // 이동할 URL 설정
            var newURL = "https://matprint.site/search";

            // 페이지를 새 URL로 이동함
            window.location.href = newURL;
        });

        // "마이페이지" 링크 클릭 이벤트 처리
        $("#myPageLink").click(function (event) {
            event.preventDefault(); // 기본 링크 동작을 막습니다.

            // 이동할 URL 설정
            var newURL = "https://matprint.site/myPage";

            // 페이지를 새 URL로 이동함
            window.location.href = newURL;
        });

        // "마이페이지" 링크 클릭 이벤트 처리
        $("#mainSectionLink").click(function (event) {
            event.preventDefault(); // 기본 링크 동작을 막습니다.

            // 이동할 URL 설정
            var newURL = "https://matprint.site";

            // 페이지를 새 URL로 이동함
            window.location.href = newURL;
        });

        // "동행페이지" 링크 클릭 이벤트 처리
        $("#mateSectionLink").click(function (event) {
            event.preventDefault(); // 기본 링크 동작을 막습니다.

            // 이동할 URL 설정
            var newURL = "https://matprint.site/mate";

            // 페이지를 새 URL로 이동함
            window.location.href = newURL;
        });

        $("body").on("click", "a.detail-link", function(event) {
            event.preventDefault(); // Prevent the default link behavior

            // Extract the data-name and data-address attributes
            var name = $(this).data("name");
            var address = $(this).data("address");

            // Check if name and address are correctly extracted (for debugging)
            console.log("Name: " + name);
            console.log("Address: " + address);

            // URL creation and page redirection
            var url = "https://matprint.site/restaurant/detail?name=" + encodeURIComponent(name) + "&address=" + encodeURIComponent(address);
            window.location.href = url;
        });
    });

})(jQuery);