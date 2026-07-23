FROM python:3.13-slim
WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY generator.py .
CMD ["python", "generator.py"]
#cdr-generator konteyneri açık kaldığı sürece Python scripti sürekli çalışır.
