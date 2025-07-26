import requests
from bs4 import BeautifulSoup
import csv
import time
from site_configs import SITE_CONFIGS
from datetime import datetime

# ----------------------
# 사이트별 파서 함수 정의
# ----------------------
def parse_thehyundaiblog(base_url):
    """
    현대백화점 블로그 팝업스토어 정보 파싱 (startDate, endDate 컬럼 포함)
    """
    results = []
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    }
    res = requests.get(base_url, headers=headers)
    soup = BeautifulSoup(res.text, "html.parser")
    links = []
    for a in soup.select(".post-item a"):  # 예시: .post-item a
        href = a.get("href")
        if href and href.startswith("http"):
            links.append(href)
    links = list(set(links))
    for link in links:
        try:
            detail_res = requests.get(link, headers=headers)
            detail_soup = BeautifulSoup(detail_res.text, "html.parser")
            title = detail_soup.select_one("h1")
            name = title.text.strip() if title else ""
            desc = detail_soup.select_one(".entry-content")
            description = desc.text.strip() if desc else ""
            img = detail_soup.select_one(".entry-content img")
            image_url = img["src"] if img and img.has_attr("src") else ""
            cat = detail_soup.select_one(".category")
            category = cat.text.strip() if cat else "POP-UP"
            # 날짜 추출 예시 (실제 구조에 맞게 수정 필요)
            # 예: "2024.07.01 ~ 2024.07.15" 형태의 텍스트에서 추출
            date_text = detail_soup.text
            start_date, end_date = "", ""
            import re
            m = re.search(r"(20\d{2}\.\d{2}\.\d{2}) ?~ ?(20\d{2}\.\d{2}\.\d{2})", date_text)
            if m:
                # yyyy.MM.dd -> yyyy-MM-dd 변환
                start_date = m.group(1).replace(".", "-")
                end_date = m.group(2).replace(".", "-")
            results.append({
                "name": name,
                "description": description,
                "imageUrl": image_url,
                "sourceUrl": link,
                "category": category,
                "startDate": start_date,
                "endDate": end_date
            })
            time.sleep(1)
        except Exception as e:
            print(f"{link} 에서 오류 발생: {e}")
    return results

def parse_othersite(base_url):
    """
    다른 사이트 예시 파서 (startDate, endDate 포함, 실제 구조에 맞게 구현 필요)
    """
    # TODO: 실제 사이트 구조에 맞게 구현
    return []

PARSERS = {
    "parse_thehyundaiblog": parse_thehyundaiblog,
    "parse_othersite": parse_othersite
}

def main():
    all_results = []
    for site in SITE_CONFIGS:
        print(f"[{site['name']}] 크롤링 시작...")
        parser_func = PARSERS[site["parser"]]
        results = parser_func(site["base_url"])
        all_results.extend(results)
    # 결과가 있으면 CSV로 저장 (startDate, endDate 포함)
    if all_results:
        with open("popupstores.csv", "w", newline="", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=all_results[0].keys())
            writer.writeheader()
            writer.writerows(all_results)
        print("popupstores.csv 파일로 저장 완료!")
    else:
        print("수집된 데이터가 없습니다.")

if __name__ == "__main__":
    main() 