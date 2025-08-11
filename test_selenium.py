#!/usr/bin/env python3
"""
Selenium автотест для Deals Platform Frontend
Тестирует end-to-end сценарий работы с платформой
"""

import time
import logging
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options
from selenium.common.exceptions import TimeoutException, NoSuchElementException

# Настройка логирования
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('selenium_test.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

class DealsPlatformTest:
    def __init__(self):
        self.driver = None
        self.wait = None
        self.base_url = "http://localhost:3000"  # Предполагаем, что frontend запущен на порту 3000
        
    def setup_driver(self):
        """Настройка Chrome WebDriver"""
        try:
            chrome_options = Options()
            chrome_options.add_argument("--no-sandbox")
            chrome_options.add_argument("--disable-dev-shm-usage")
            chrome_options.add_argument("--disable-gpu")
            chrome_options.add_argument("--window-size=1920,1080")
            
            # Для headless режима раскомментировать:
            # chrome_options.add_argument("--headless")
            
            self.driver = webdriver.Chrome(options=chrome_options)
            self.wait = WebDriverWait(self.driver, 10)
            
            logger.info("WebDriver успешно инициализирован")
            return True
        except Exception as e:
            logger.error(f"Ошибка инициализации WebDriver: {e}")
            return False
    
    def teardown_driver(self):
        """Закрытие WebDriver"""
        if self.driver:
            self.driver.quit()
            logger.info("WebDriver закрыт")
    
    def log_step(self, step_name):
        """Логирование шага теста"""
        logger.info(f"=== ШАГ: {step_name} ===")
    
    def wait_and_find_element(self, by, value, timeout=10):
        """Ожидание и поиск элемента"""
        try:
            element = WebDriverWait(self.driver, timeout).until(
                EC.presence_of_element_located((by, value))
            )
            return element
        except TimeoutException:
            logger.error(f"Элемент не найден: {by}={value}")
            return None
    
    def wait_and_click(self, by, value, timeout=10):
        """Ожидание и клик по элементу"""
        try:
            element = WebDriverWait(self.driver, timeout).until(
                EC.element_to_be_clickable((by, value))
            )
            element.click()
            return True
        except TimeoutException:
            logger.error(f"Элемент не кликабелен: {by}={value}")
            return False
    
    def wait_and_send_keys(self, by, value, text, timeout=10):
        """Ожидание и ввод текста"""
        element = self.wait_and_find_element(by, value, timeout)
        if element:
            element.clear()
            element.send_keys(text)
            return True
        return False
    
    def test_registration(self):
        """Тест регистрации пользователя"""
        self.log_step("Тест регистрации пользователя")
        
        try:
            # Открыть страницу
            self.driver.get(self.base_url)
            logger.info("Страница открыта")
            
            # Нажать кнопку регистрации
            if not self.wait_and_click(By.ID, "nav-register"):
                return False
            
            # Заполнить форму регистрации
            test_email = f"test_user_{int(time.time())}@example.com"
            test_password = "password123"
            
            if not self.wait_and_send_keys(By.ID, "register-email", test_email):
                return False
            
            if not self.wait_and_send_keys(By.ID, "register-password", test_password):
                return False
            
            if not self.wait_and_send_keys(By.ID, "register-confirm-password", test_password):
                return False
            
            # Отправить форму
            if not self.wait_and_click(By.CSS_SELECTOR, "#register-form button[type='submit']"):
                return False
            
            # Проверить успешную регистрацию
            time.sleep(2)
            
            # Проверить, что пользователь залогинен
            user_email_element = self.wait_and_find_element(By.ID, "user-email")
            if user_email_element and test_email in user_email_element.text:
                logger.info("Регистрация успешна")
                return True
            else:
                logger.error("Регистрация не удалась")
                return False
                
        except Exception as e:
            logger.error(f"Ошибка в тесте регистрации: {e}")
            return False
    
    def test_login(self):
        """Тест входа пользователя"""
        self.log_step("Тест входа пользователя")
        
        try:
            # Сначала выйти, если залогинены
            try:
                logout_btn = self.driver.find_element(By.ID, "nav-logout")
                logout_btn.click()
                time.sleep(1)
            except NoSuchElementException:
                pass
            
            # Нажать кнопку входа
            if not self.wait_and_click(By.ID, "nav-login"):
                return False
            
            # Заполнить форму входа
            test_email = "alice@example.com"  # Используем тестового пользователя
            test_password = "password123"
            
            if not self.wait_and_send_keys(By.ID, "login-email", test_email):
                return False
            
            if not self.wait_and_send_keys(By.ID, "login-password", test_password):
                return False
            
            # Отправить форму
            if not self.wait_and_click(By.CSS_SELECTOR, "#login-form button[type='submit']"):
                return False
            
            # Проверить успешный вход
            time.sleep(2)
            
            user_email_element = self.wait_and_find_element(By.ID, "user-email")
            if user_email_element and test_email in user_email_element.text:
                logger.info("Вход успешен")
                return True
            else:
                logger.error("Вход не удался")
                return False
                
        except Exception as e:
            logger.error(f"Ошибка в тесте входа: {e}")
            return False
    
    def test_create_deal(self):
        """Тест создания пари"""
        self.log_step("Тест создания пари")
        
        try:
            # Перейти на страницу создания пари
            if not self.wait_and_click(By.ID, "nav-create-deal"):
                return False
            
            # Заполнить форму создания пари
            deal_description = f"Тестовое пари {int(time.time())}"
            
            if not self.wait_and_send_keys(By.ID, "deal-description", deal_description):
                return False
            
            # Установить время старта (через час)
            start_time = time.strftime("%Y-%m-%dT%H:%M", time.localtime(time.time() + 3600))
            if not self.wait_and_send_keys(By.ID, "deal-start-time", start_time):
                return False
            
            # Установить длительность
            if not self.wait_and_send_keys(By.ID, "deal-duration", "30"):
                return False
            
            # Отправить форму
            if not self.wait_and_click(By.CSS_SELECTOR, "#create-deal-form button[type='submit']"):
                return False
            
            # Проверить успешное создание
            time.sleep(2)
            
            # Проверить, что вернулись на дашборд
            dashboard_page = self.wait_and_find_element(By.ID, "dashboard-page")
            if dashboard_page and not dashboard_page.get_attribute("class").contains("d-none"):
                logger.info("Пари успешно создано")
                return True
            else:
                logger.error("Создание пари не удалось")
                return False
                
        except Exception as e:
            logger.error(f"Ошибка в тесте создания пари: {e}")
            return False
    
    def test_join_deal(self):
        """Тест присоединения к пари"""
        self.log_step("Тест присоединения к пари")
        
        try:
            # Перейти на дашборд
            if not self.wait_and_click(By.ID, "nav-dashboard"):
                return False
            
            time.sleep(2)
            
            # Найти кнопку "Участвовать" для первого пари
            participate_buttons = self.driver.find_elements(By.XPATH, "//button[contains(text(), 'Участвовать')]")
            
            if participate_buttons:
                # Кликнуть по первой кнопке
                participate_buttons[0].click()
                time.sleep(2)
                
                # Проверить, что кнопка изменилась
                updated_buttons = self.driver.find_elements(By.XPATH, "//button[contains(text(), 'Участвовать')]")
                if len(updated_buttons) < len(participate_buttons):
                    logger.info("Успешно присоединились к пари")
                    return True
                else:
                    logger.error("Присоединение к пари не удалось")
                    return False
            else:
                logger.info("Нет доступных пари для участия")
                return True
                
        except Exception as e:
            logger.error(f"Ошибка в тесте присоединения к пари: {e}")
            return False
    
    def test_view_deal_details(self):
        """Тест просмотра деталей пари"""
        self.log_step("Тест просмотра деталей пари")
        
        try:
            # Найти кнопку "Просмотр" для первого пари
            view_buttons = self.driver.find_elements(By.XPATH, "//button[contains(text(), 'Просмотр')]")
            
            if view_buttons:
                # Кликнуть по первой кнопке
                view_buttons[0].click()
                time.sleep(2)
                
                # Проверить, что открылась страница деталей
                deal_detail_page = self.wait_and_find_element(By.ID, "deal-detail-page")
                if deal_detail_page and not deal_detail_page.get_attribute("class").contains("d-none"):
                    logger.info("Детали пари успешно загружены")
                    return True
                else:
                    logger.error("Загрузка деталей пари не удалась")
                    return False
            else:
                logger.info("Нет пари для просмотра")
                return True
                
        except Exception as e:
            logger.error(f"Ошибка в тесте просмотра деталей пари: {e}")
            return False
    
    def test_navigation(self):
        """Тест навигации по приложению"""
        self.log_step("Тест навигации")
        
        try:
            # Тест перехода на дашборд
            if not self.wait_and_click(By.ID, "nav-dashboard"):
                return False
            
            time.sleep(1)
            dashboard_page = self.wait_and_find_element(By.ID, "dashboard-page")
            if not dashboard_page or dashboard_page.get_attribute("class").contains("d-none"):
                logger.error("Навигация на дашборд не работает")
                return False
            
            # Тест перехода на страницу создания пари
            if not self.wait_and_click(By.ID, "nav-create-deal"):
                return False
            
            time.sleep(1)
            create_deal_page = self.wait_and_find_element(By.ID, "create-deal-page")
            if not create_deal_page or create_deal_page.get_attribute("class").contains("d-none"):
                logger.error("Навигация на страницу создания пари не работает")
                return False
            
            logger.info("Навигация работает корректно")
            return True
            
        except Exception as e:
            logger.error(f"Ошибка в тесте навигации: {e}")
            return False
    
    def run_full_test(self):
        """Запуск полного теста"""
        logger.info("Начинаем полный тест Deals Platform")
        
        if not self.setup_driver():
            logger.error("Не удалось инициализировать WebDriver")
            return False
        
        try:
            test_results = []
            
            # Запуск всех тестов
            tests = [
                ("Регистрация", self.test_registration),
                ("Вход", self.test_login),
                ("Создание пари", self.test_create_deal),
                ("Присоединение к пари", self.test_join_deal),
                ("Просмотр деталей пари", self.test_view_deal_details),
                ("Навигация", self.test_navigation)
            ]
            
            for test_name, test_func in tests:
                try:
                    result = test_func()
                    test_results.append((test_name, result))
                    if result:
                        logger.info(f"✅ {test_name}: ПРОЙДЕН")
                    else:
                        logger.error(f"❌ {test_name}: ПРОВАЛЕН")
                except Exception as e:
                    logger.error(f"❌ {test_name}: ОШИБКА - {e}")
                    test_results.append((test_name, False))
                
                time.sleep(2)  # Пауза между тестами
            
            # Итоговый отчет
            logger.info("\n" + "="*50)
            logger.info("ИТОГОВЫЙ ОТЧЕТ")
            logger.info("="*50)
            
            passed = sum(1 for _, result in test_results if result)
            total = len(test_results)
            
            for test_name, result in test_results:
                status = "✅ ПРОЙДЕН" if result else "❌ ПРОВАЛЕН"
                logger.info(f"{test_name}: {status}")
            
            logger.info(f"\nВсего тестов: {total}")
            logger.info(f"Пройдено: {passed}")
            logger.info(f"Провалено: {total - passed}")
            logger.info(f"Процент успеха: {(passed/total)*100:.1f}%")
            
            return passed == total
            
        except Exception as e:
            logger.error(f"Критическая ошибка в тесте: {e}")
            return False
        
        finally:
            self.teardown_driver()

def main():
    """Главная функция"""
    print("Deals Platform - Selenium Автотест")
    print("="*40)
    
    # Проверка, что backend запущен
    print("Убедитесь, что:")
    print("1. Backend запущен на http://localhost:8080")
    print("2. Frontend запущен на http://localhost:3000")
    print("3. PostgreSQL запущен")
    print("4. Chrome WebDriver установлен")
    print()
    
    input("Нажмите Enter для запуска тестов...")
    
    # Запуск тестов
    test = DealsPlatformTest()
    success = test.run_full_test()
    
    if success:
        print("\n🎉 Все тесты пройдены успешно!")
    else:
        print("\n💥 Некоторые тесты провалились. Проверьте лог для деталей.")
    
    print(f"\nЛог сохранен в файл: selenium_test.log")

if __name__ == "__main__":
    main()
