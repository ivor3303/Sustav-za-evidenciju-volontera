@echo off
cd /d "%~dp0"

echo ============================================
echo  Postavljanje Git repozitorija
echo ============================================
echo.

where git >nul 2>nul
if errorlevel 1 (
    echo GRESKA: Git nije pronadjen. Instaliraj Git for Windows sa git-scm.com i pokreni ponovno.
    pause
    exit /b 1
)

if not exist ".git" (
    echo Inicijaliziram git repozitorij...
    git init
) else (
    echo Git repozitorij vec postoji, preskacem init.
)

git branch -M main

echo.
echo Postavljam remote (origin)...
git remote remove origin >nul 2>nul
git remote add origin https://github.com/ivor3303/Sustav-za-evidenciju-volontera.git

echo.
echo Dodajem fajlove (target/, .idea/, *.db su ignorirani preko .gitignore)...
git add .

echo.
echo Kreiram commit...
git commit -m "Initial commit: konzolna i JavaFX verzija sustava za evidenciju volontera"

echo.
echo ============================================
echo  Pushanje na GitHub
echo ============================================
echo Ovo ce PREPISATI trenutni sadrzaj na GitHubu (force push).
echo Ako se otvori prozor za prijavu, prijavi se sa svojim GitHub racunom.
echo.
pause

git push -u origin main --force

echo.
echo ============================================
echo  Gotovo! Provjeri repozitorij na GitHubu.
echo ============================================
pause
