SITE_CONFIGS = [
    {
        "name": "thehyundaiblog",
        "base_url": "https://thehyundaiblog.com/category/POP-UP",
        "parser": "parse_thehyundaiblog"
    },
    {
        "name": "othersite",
        "base_url": "https://othersite.com/popup",
        "parser": "parse_othersite"
    }
    # 필요한 만큼 사이트 추가
]

# [중요] 크롤러는 startDate, endDate(yyyy-MM-dd 또는 yyyy-MM-dd HH:mm:ss) 컬럼을 반드시 포함해야 함 