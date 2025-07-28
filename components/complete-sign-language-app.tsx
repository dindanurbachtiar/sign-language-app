"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Badge } from "@/components/ui/badge"
import {
  ArrowLeft,
  ArrowRight,
  Camera,
  Volume2,
  RotateCcw,
  Play,
  Home,
  Trophy,
  Star,
  CheckCircle,
  XCircle,
  RefreshCw,
  BookOpen,
  Target,
  Users,
  Heart,
} from "lucide-react"

type Screen = "splash" | "home" | "learn" | "ar" | "quiz" | "about" | "profile" | "achievements"
type QuizMode = "letter-to-sign" | "sign-to-letter" | "mixed"
type Difficulty = "easy" | "medium" | "hard"

interface UserProgress {
  lettersLearned: string[]
  quizScores: { [key: string]: number }
  totalPoints: number
  achievements: string[]
  currentStreak: number
  bestStreak: number
}

interface Letter {
  letter: string
  description: string
  gesture: string
  difficulty: Difficulty
  tips: string
  commonMistakes: string[]
}

export function CompleteSignLanguageApp() {
  const [currentScreen, setCurrentScreen] = useState<Screen>("splash")
  const [currentLetter, setCurrentLetter] = useState(0)
  const [userProgress, setUserProgress] = useState<UserProgress>({
    lettersLearned: [],
    quizScores: {},
    totalPoints: 0,
    achievements: [],
    currentStreak: 0,
    bestStreak: 0,
  })
  const [quizMode, setQuizMode] = useState<QuizMode>("letter-to-sign")
  const [currentQuizQuestion, setCurrentQuizQuestion] = useState(0)
  const [quizScore, setQuizScore] = useState(0)
  const [quizAnswers, setQuizAnswers] = useState<boolean[]>([])
  const [showQuizResult, setShowQuizResult] = useState(false)
  const [arMode, setArMode] = useState<"scanning" | "displaying" | "idle">("idle")
  const [selectedArLetter, setSelectedArLetter] = useState<string | null>(null)

  const letters: Letter[] = [
    {
      letter: "A",
      description: "Jari telunjuk ke atas",
      gesture: "👆",
      difficulty: "easy",
      tips: "Pastikan jari telunjuk lurus ke atas",
      commonMistakes: ["Jari bengkok", "Posisi tangan miring"],
    },
    {
      letter: "B",
      description: "Telapak tangan terbuka",
      gesture: "✋",
      difficulty: "easy",
      tips: "Semua jari rapat dan lurus",
      commonMistakes: ["Jari terpisah", "Telapak tidak rata"],
    },
    {
      letter: "C",
      description: "Bentuk huruf C",
      gesture: "👌",
      difficulty: "medium",
      tips: "Bentuk lingkaran dengan jari",
      commonMistakes: ["Lingkaran terlalu kecil", "Jari tidak rapat"],
    },
    {
      letter: "D",
      description: "Jari telunjuk tegak",
      gesture: "☝️",
      difficulty: "easy",
      tips: "Hanya jari telunjuk yang tegak",
      commonMistakes: ["Jari lain ikut tegak", "Posisi miring"],
    },
    {
      letter: "E",
      description: "Kepalan tangan",
      gesture: "✊",
      difficulty: "easy",
      tips: "Semua jari mengepal rapat",
      commonMistakes: ["Kepalan longgar", "Ibu jari keluar"],
    },
    {
      letter: "F",
      description: "Jari telunjuk dan jempol menyentuh",
      gesture: "👌",
      difficulty: "medium",
      tips: "Bentuk lingkaran kecil",
      commonMistakes: ["Jari tidak menyentuh", "Lingkaran terlalu besar"],
    },
    {
      letter: "G",
      description: "Jari telunjuk dan jempol horizontal",
      gesture: "👈",
      difficulty: "medium",
      tips: "Posisi horizontal sejajar",
      commonMistakes: ["Posisi vertikal", "Jari tidak sejajar"],
    },
    {
      letter: "H",
      description: "Dua jari horizontal",
      gesture: "✌️",
      difficulty: "medium",
      tips: "Jari telunjuk dan tengah horizontal",
      commonMistakes: ["Posisi vertikal", "Jari bengkok"],
    },
    {
      letter: "I",
      description: "Jari kelingking tegak",
      gesture: "🤙",
      difficulty: "hard",
      tips: "Hanya kelingking yang tegak",
      commonMistakes: ["Jari lain ikut tegak", "Kelingking bengkok"],
    },
    {
      letter: "J",
      description: "Gerakan huruf J",
      gesture: "🤙",
      difficulty: "hard",
      tips: "Gerakan melengkung seperti J",
      commonMistakes: ["Gerakan lurus", "Arah salah"],
    },
    {
      letter: "K",
      description: "Jari telunjuk dan tengah tegak",
      gesture: "✌️",
      difficulty: "medium",
      tips: "Dua jari tegak, yang lain mengepal",
      commonMistakes: ["Semua jari tegak", "Jari bengkok"],
    },
    {
      letter: "L",
      description: "Bentuk huruf L",
      gesture: "🤟",
      difficulty: "medium",
      tips: "Jempol dan telunjuk membentuk L",
      commonMistakes: ["Sudut tidak siku", "Jari bengkok"],
    },
    {
      letter: "M",
      description: "Tiga jari di atas jempol",
      gesture: "✊",
      difficulty: "hard",
      tips: "Tiga jari pertama di atas jempol",
      commonMistakes: ["Posisi jari salah", "Jempol tidak tertutup"],
    },
    {
      letter: "N",
      description: "Dua jari di atas jempol",
      gesture: "✊",
      difficulty: "hard",
      tips: "Telunjuk dan tengah di atas jempol",
      commonMistakes: ["Tiga jari di atas", "Posisi salah"],
    },
    {
      letter: "O",
      description: "Bentuk lingkaran",
      gesture: "👌",
      difficulty: "easy",
      tips: "Semua jari membentuk lingkaran",
      commonMistakes: ["Lingkaran tidak bulat", "Jari tidak rapat"],
    },
    {
      letter: "P",
      description: "Seperti K tapi menunjuk ke bawah",
      gesture: "👇",
      difficulty: "hard",
      tips: "Posisi K dibalik ke bawah",
      commonMistakes: ["Arah ke atas", "Posisi jari salah"],
    },
    {
      letter: "Q",
      description: "Seperti G tapi menunjuk ke bawah",
      gesture: "👇",
      difficulty: "hard",
      tips: "Posisi G dibalik ke bawah",
      commonMistakes: ["Arah ke atas", "Jari tidak tepat"],
    },
    {
      letter: "R",
      description: "Jari telunjuk dan tengah menyilang",
      gesture: "🤞",
      difficulty: "medium",
      tips: "Dua jari saling menyilang",
      commonMistakes: ["Jari tidak menyilang", "Posisi salah"],
    },
    {
      letter: "S",
      description: "Kepalan dengan jempol di depan",
      gesture: "✊",
      difficulty: "medium",
      tips: "Jempol menutupi jari lain",
      commonMistakes: ["Jempol di samping", "Kepalan longgar"],
    },
    {
      letter: "T",
      description: "Jempol di antara telunjuk dan tengah",
      gesture: "✊",
      difficulty: "hard",
      tips: "Jempol terjepit di antara jari",
      commonMistakes: ["Jempol di luar", "Posisi salah"],
    },
    {
      letter: "U",
      description: "Dua jari tegak rapat",
      gesture: "✌️",
      difficulty: "medium",
      tips: "Telunjuk dan tengah rapat tegak",
      commonMistakes: ["Jari terpisah", "Jari bengkok"],
    },
    {
      letter: "V",
      description: "Dua jari tegak terpisah",
      gesture: "✌️",
      difficulty: "easy",
      tips: "Telunjuk dan tengah terpisah",
      commonMistakes: ["Jari rapat", "Jari bengkok"],
    },
    {
      letter: "W",
      description: "Tiga jari tegak",
      gesture: "🤟",
      difficulty: "medium",
      tips: "Tiga jari pertama tegak",
      commonMistakes: ["Dua jari saja", "Jari bengkok"],
    },
    {
      letter: "X",
      description: "Jari telunjuk bengkok",
      gesture: "☝️",
      difficulty: "hard",
      tips: "Telunjuk bengkok seperti kait",
      commonMistakes: ["Jari lurus", "Bengkokan salah"],
    },
    {
      letter: "Y",
      description: "Jempol dan kelingking tegak",
      gesture: "🤙",
      difficulty: "medium",
      tips: "Hanya jempol dan kelingking tegak",
      commonMistakes: ["Jari lain ikut", "Posisi salah"],
    },
    {
      letter: "Z",
      description: "Gerakan zigzag",
      gesture: "👆",
      difficulty: "hard",
      tips: "Gerakan membentuk huruf Z",
      commonMistakes: ["Gerakan lurus", "Arah salah"],
    },
  ]

  const achievements = [
    { id: "first_letter", name: "Huruf Pertama", description: "Pelajari huruf pertama", icon: "🎯", unlocked: false },
    { id: "five_letters", name: "Lima Huruf", description: "Pelajari 5 huruf", icon: "⭐", unlocked: false },
    { id: "alphabet_master", name: "Master Alfabet", description: "Pelajari semua huruf", icon: "👑", unlocked: false },
    { id: "quiz_champion", name: "Juara Kuis", description: "Skor sempurna dalam kuis", icon: "🏆", unlocked: false },
    {
      id: "streak_master",
      name: "Master Streak",
      description: "Streak 7 hari berturut-turut",
      icon: "🔥",
      unlocked: false,
    },
    { id: "ar_explorer", name: "Penjelajah AR", description: "Gunakan mode AR 10 kali", icon: "📱", unlocked: false },
  ]

  const quizQuestions = [
    {
      type: "letter-to-sign" as const,
      question: "Pilih isyarat yang benar untuk huruf 'A'",
      options: ["👆", "✋", "👌", "☝️"],
      correct: 0,
      letter: "A",
    },
    {
      type: "sign-to-letter" as const,
      question: "Huruf apa yang ditunjukkan isyarat ini: ✋",
      options: ["A", "B", "C", "D"],
      correct: 1,
      gesture: "✋",
    },
    {
      type: "letter-to-sign" as const,
      question: "Pilih isyarat yang benar untuk huruf 'C'",
      options: ["👌", "✊", "✌️", "🤙"],
      correct: 0,
      letter: "C",
    },
    {
      type: "sign-to-letter" as const,
      question: "Huruf apa yang ditunjukkan isyarat ini: ✌️",
      options: ["U", "V", "W", "X"],
      correct: 1,
      gesture: "✌️",
    },
    {
      type: "letter-to-sign" as const,
      question: "Pilih isyarat yang benar untuk huruf 'O'",
      options: ["👌", "✊", "🤙", "👆"],
      correct: 0,
      letter: "O",
    },
  ]

  useEffect(() => {
    // Load user progress from localStorage
    const savedProgress = localStorage.getItem("signLanguageProgress")
    if (savedProgress) {
      setUserProgress(JSON.parse(savedProgress))
    }
  }, [])

  useEffect(() => {
    // Save user progress to localStorage
    localStorage.setItem("signLanguageProgress", JSON.stringify(userProgress))
  }, [userProgress])

  const markLetterAsLearned = (letter: string) => {
    if (!userProgress.lettersLearned.includes(letter)) {
      const newProgress = {
        ...userProgress,
        lettersLearned: [...userProgress.lettersLearned, letter],
        totalPoints: userProgress.totalPoints + 10,
        currentStreak: userProgress.currentStreak + 1,
      }
      setUserProgress(newProgress)
      checkAchievements(newProgress)
    }
  }

  const checkAchievements = (progress: UserProgress) => {
    const newAchievements = [...progress.achievements]

    if (progress.lettersLearned.length >= 1 && !newAchievements.includes("first_letter")) {
      newAchievements.push("first_letter")
    }
    if (progress.lettersLearned.length >= 5 && !newAchievements.includes("five_letters")) {
      newAchievements.push("five_letters")
    }
    if (progress.lettersLearned.length >= 26 && !newAchievements.includes("alphabet_master")) {
      newAchievements.push("alphabet_master")
    }

    if (newAchievements.length > progress.achievements.length) {
      setUserProgress({ ...progress, achievements: newAchievements })
    }
  }

  const SplashScreen = () => (
    <div className="min-h-screen bg-gradient-to-br from-cyan-200 to-cyan-300 flex flex-col items-center justify-center p-6">
      <div className="text-center space-y-8">
        <div className="text-8xl mb-4 animate-bounce">🤟</div>
        <h1 className="text-4xl font-bold text-gray-800 mb-2">ISYARAT</h1>
        <h2 className="text-2xl font-bold text-gray-800">PINTAR</h2>
        <p className="text-gray-600 max-w-xs">
          Aplikasi pembelajaran bahasa isyarat Indonesia (SIBI) dengan teknologi AR
        </p>
        <div className="w-16 h-1 bg-gray-600 mx-auto mt-8"></div>
      </div>
      <Button
        onClick={() => setCurrentScreen("home")}
        className="mt-12 bg-blue-500 hover:bg-blue-600 text-white px-8 py-3 rounded-full text-lg"
      >
        Mulai Belajar
      </Button>
    </div>
  )

  const HomeScreen = () => (
    <div className="min-h-screen bg-gradient-to-br from-yellow-200 to-yellow-300 p-6">
      <div className="max-w-md mx-auto">
        <div className="text-center mb-8">
          <div className="flex items-center justify-between mb-4">
            <div></div>
            <h1 className="text-2xl font-bold text-gray-800">Selamat Datang!</h1>
            <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("profile")}>
              <Users className="h-6 w-6" />
            </Button>
          </div>
          <h2 className="text-xl font-semibold text-gray-700 mb-4">Ayo Belajar Bahasa Isyarat!</h2>

          <Card className="bg-white/80 mb-6">
            <CardContent className="p-4">
              <div className="flex justify-between items-center">
                <div className="text-left">
                  <p className="text-sm text-gray-600">Progress Pembelajaran</p>
                  <p className="text-lg font-bold">{userProgress.lettersLearned.length}/26 Huruf</p>
                </div>
                <div className="text-right">
                  <p className="text-sm text-gray-600">Total Poin</p>
                  <p className="text-lg font-bold text-yellow-600">{userProgress.totalPoints}</p>
                </div>
              </div>
              <Progress value={(userProgress.lettersLearned.length / 26) * 100} className="mt-2" />
            </CardContent>
          </Card>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <Card
            className="bg-blue-400 text-white cursor-pointer hover:bg-blue-500 transition-all transform hover:scale-105"
            onClick={() => setCurrentScreen("learn")}
          >
            <CardContent className="p-6 text-center">
              <BookOpen className="h-8 w-8 mx-auto mb-2" />
              <h3 className="font-semibold">Belajar Huruf</h3>
              <p className="text-xs opacity-80 mt-1">{userProgress.lettersLearned.length}/26 selesai</p>
            </CardContent>
          </Card>

          <Card
            className="bg-red-400 text-white cursor-pointer hover:bg-red-500 transition-all transform hover:scale-105"
            onClick={() => setCurrentScreen("ar")}
          >
            <CardContent className="p-6 text-center">
              <Camera className="h-8 w-8 mx-auto mb-2" />
              <h3 className="font-semibold">AR Mode</h3>
              <p className="text-xs opacity-80 mt-1">Visualisasi 3D</p>
            </CardContent>
          </Card>

          <Card
            className="bg-green-400 text-white cursor-pointer hover:bg-green-500 transition-all transform hover:scale-105"
            onClick={() => setCurrentScreen("quiz")}
          >
            <CardContent className="p-6 text-center">
              <Target className="h-8 w-8 mx-auto mb-2" />
              <h3 className="font-semibold">Kuis</h3>
              <p className="text-xs opacity-80 mt-1">Uji kemampuan</p>
            </CardContent>
          </Card>

          <Card
            className="bg-purple-400 text-white cursor-pointer hover:bg-purple-500 transition-all transform hover:scale-105"
            onClick={() => setCurrentScreen("about")}
          >
            <CardContent className="p-6 text-center">
              <Heart className="h-8 w-8 mx-auto mb-2" />
              <h3 className="font-semibold">Tentang</h3>
              <p className="text-xs opacity-80 mt-1">Info aplikasi</p>
            </CardContent>
          </Card>
        </div>

        {userProgress.achievements.length > 0 && (
          <Card className="mt-6 bg-white/80">
            <CardContent className="p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-semibold">Pencapaian Terbaru</p>
                  <p className="text-xs text-gray-600">{userProgress.achievements.length} pencapaian dibuka</p>
                </div>
                <Button variant="outline" size="sm" onClick={() => setCurrentScreen("achievements")}>
                  <Trophy className="h-4 w-4 mr-1" />
                  Lihat
                </Button>
              </div>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  )

  const LearnScreen = () => {
    const currentLetterData = letters[currentLetter]
    const isLearned = userProgress.lettersLearned.includes(currentLetterData.letter)

    return (
      <div className="min-h-screen bg-gradient-to-br from-cyan-200 to-cyan-300 p-6">
        <div className="max-w-md mx-auto">
          <div className="flex items-center justify-between mb-6">
            <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("home")}>
              <ArrowLeft className="h-6 w-6" />
            </Button>
            <h1 className="text-2xl font-bold text-gray-800">Belajar Huruf</h1>
            <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("home")}>
              <Home className="h-6 w-6" />
            </Button>
          </div>

          <div className="flex justify-center mb-6 space-x-1 overflow-x-auto pb-2">
            {letters.slice(0, 13).map((letter, index) => (
              <Button
                key={index}
                variant={currentLetter === index ? "default" : "outline"}
                size="sm"
                onClick={() => setCurrentLetter(index)}
                className={`w-8 h-8 rounded-full flex-shrink-0 ${
                  userProgress.lettersLearned.includes(letter.letter) ? "bg-green-100 border-green-400" : ""
                }`}
              >
                {letter.letter}
                {userProgress.lettersLearned.includes(letter.letter) && (
                  <CheckCircle className="h-3 w-3 text-green-600 absolute -top-1 -right-1" />
                )}
              </Button>
            ))}
          </div>

          <div className="flex justify-center mb-6 space-x-1 overflow-x-auto pb-2">
            {letters.slice(13, 26).map((letter, index) => (
              <Button
                key={index + 13}
                variant={currentLetter === index + 13 ? "default" : "outline"}
                size="sm"
                onClick={() => setCurrentLetter(index + 13)}
                className={`w-8 h-8 rounded-full flex-shrink-0 ${
                  userProgress.lettersLearned.includes(letter.letter) ? "bg-green-100 border-green-400" : ""
                }`}
              >
                {letter.letter}
                {userProgress.lettersLearned.includes(letter.letter) && (
                  <CheckCircle className="h-3 w-3 text-green-600 absolute -top-1 -right-1" />
                )}
              </Button>
            ))}
          </div>

          <Card className="bg-white mb-6">
            <CardContent className="p-8 text-center">
              <div className="flex justify-between items-start mb-4">
                <Badge
                  variant={
                    currentLetterData.difficulty === "easy"
                      ? "default"
                      : currentLetterData.difficulty === "medium"
                        ? "secondary"
                        : "destructive"
                  }
                >
                  {currentLetterData.difficulty === "easy"
                    ? "Mudah"
                    : currentLetterData.difficulty === "medium"
                      ? "Sedang"
                      : "Sulit"}
                </Badge>
                {isLearned && <CheckCircle className="h-6 w-6 text-green-600" />}
              </div>

              <div className="text-8xl mb-4">{currentLetterData.gesture}</div>
              <h2 className="text-3xl font-bold text-gray-800 mb-2">{currentLetterData.letter}</h2>
              <p className="text-gray-600 mb-4">
                {currentLetterData.letter} = {currentLetterData.description}
              </p>

              <div className="bg-blue-50 p-4 rounded-lg mb-4">
                <h4 className="font-semibold text-blue-800 mb-2">💡 Tips:</h4>
                <p className="text-sm text-blue-700">{currentLetterData.tips}</p>
              </div>

              {currentLetterData.commonMistakes.length > 0 && (
                <div className="bg-red-50 p-4 rounded-lg mb-4">
                  <h4 className="font-semibold text-red-800 mb-2">⚠️ Kesalahan Umum:</h4>
                  <ul className="text-sm text-red-700 text-left">
                    {currentLetterData.commonMistakes.map((mistake, index) => (
                      <li key={index}>• {mistake}</li>
                    ))}
                  </ul>
                </div>
              )}

              <div className="flex justify-center space-x-4">
                <Button className="bg-blue-500 hover:bg-blue-600 text-white px-6 py-2 rounded-full">
                  <Volume2 className="h-4 w-4 mr-2" />
                  Dengar Suara
                </Button>
                {!isLearned && (
                  <Button
                    onClick={() => markLetterAsLearned(currentLetterData.letter)}
                    className="bg-green-500 hover:bg-green-600 text-white px-6 py-2 rounded-full"
                  >
                    <CheckCircle className="h-4 w-4 mr-2" />
                    Tandai Selesai
                  </Button>
                )}
              </div>
            </CardContent>
          </Card>

          <div className="flex justify-between">
            <Button
              variant="outline"
              onClick={() => setCurrentLetter(Math.max(0, currentLetter - 1))}
              disabled={currentLetter === 0}
              className="flex-1 mr-2"
            >
              <ArrowLeft className="h-4 w-4 mr-2" />
              Sebelumnya
            </Button>
            <Button
              onClick={() => setCurrentLetter(Math.min(letters.length - 1, currentLetter + 1))}
              disabled={currentLetter === letters.length - 1}
              className="bg-blue-500 hover:bg-blue-600 flex-1 ml-2"
            >
              Lanjut
              <ArrowRight className="h-4 w-4 ml-2" />
            </Button>
          </div>
        </div>
      </div>
    )
  }

  const ARScreen = () => (
    <div className="min-h-screen bg-gradient-to-br from-gray-400 to-gray-500 p-6">
      <div className="max-w-md mx-auto">
        <div className="flex items-center justify-between mb-6">
          <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("home")} className="text-white">
            <ArrowLeft className="h-6 w-6" />
          </Button>
          <h1 className="text-2xl font-bold text-white">AR Mode</h1>
          <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("home")} className="text-white">
            <Home className="h-6 w-6" />
          </Button>
        </div>

        {arMode === "idle" && (
          <>
            <Card className="bg-white/90 mb-6">
              <CardContent className="p-6 text-center">
                <Camera className="h-16 w-16 mx-auto mb-4 text-gray-600" />
                <h3 className="text-xl font-semibold mb-2">Mode Augmented Reality</h3>
                <p className="text-gray-700 mb-4">
                  Arahkan kamera ke kartu isyarat atau pilih huruf untuk melihat animasi 3D
                </p>
                <div className="flex justify-center space-x-4">
                  <Button className="bg-blue-500 hover:bg-blue-600 text-white" onClick={() => setArMode("scanning")}>
                    <Camera className="h-4 w-4 mr-2" />
                    Scan Kartu
                  </Button>
                  <Button variant="outline" onClick={() => setArMode("displaying")}>
                    <Play className="h-4 w-4 mr-2" />
                    Pilih Huruf
                  </Button>
                </div>
              </CardContent>
            </Card>

            <Card className="bg-blue-50">
              <CardContent className="p-6">
                <h4 className="font-semibold text-blue-800 mb-3">📋 Cara Menggunakan AR:</h4>
                <ul className="text-sm text-blue-700 space-y-2">
                  <li>1. Pastikan pencahayaan cukup</li>
                  <li>2. Arahkan kamera ke kartu isyarat</li>
                  <li>3. Tunggu hingga kartu terdeteksi</li>
                  <li>4. Lihat animasi 3D yang muncul</li>
                  <li>5. Ikuti gerakan yang ditampilkan</li>
                </ul>
              </CardContent>
            </Card>
          </>
        )}

        {arMode === "scanning" && (
          <Card className="bg-black text-white">
            <CardContent className="p-6 text-center min-h-96 flex flex-col justify-center">
              <div className="border-2 border-white border-dashed rounded-lg p-8 mb-4">
                <Camera className="h-16 w-16 mx-auto mb-4" />
                <p className="text-lg mb-2">Scanning...</p>
                <p className="text-sm opacity-80">Arahkan kamera ke kartu isyarat</p>
              </div>
              <div className="flex justify-center space-x-4">
                <Button variant="outline" onClick={() => setArMode("idle")}>
                  <ArrowLeft className="h-4 w-4 mr-2" />
                  Kembali
                </Button>
                <Button
                  className="bg-green-500 hover:bg-green-600"
                  onClick={() => {
                    setSelectedArLetter("A")
                    setArMode("displaying")
                  }}
                >
                  Simulasi Deteksi
                </Button>
              </div>
            </CardContent>
          </Card>
        )}

        {arMode === "displaying" && (
          <>
            <Card className="bg-white mb-6">
              <CardContent className="p-6">
                <div className="text-center mb-4">
                  <h3 className="text-xl font-semibold mb-2">Pilih Huruf untuk AR</h3>
                  <div className="grid grid-cols-6 gap-2">
                    {letters.slice(0, 12).map((letter) => (
                      <Button
                        key={letter.letter}
                        variant={selectedArLetter === letter.letter ? "default" : "outline"}
                        size="sm"
                        onClick={() => setSelectedArLetter(letter.letter)}
                        className="w-10 h-10"
                      >
                        {letter.letter}
                      </Button>
                    ))}
                  </div>
                  <div className="grid grid-cols-6 gap-2 mt-2">
                    {letters.slice(12, 24).map((letter) => (
                      <Button
                        key={letter.letter}
                        variant={selectedArLetter === letter.letter ? "default" : "outline"}
                        size="sm"
                        onClick={() => setSelectedArLetter(letter.letter)}
                        className="w-10 h-10"
                      >
                        {letter.letter}
                      </Button>
                    ))}
                  </div>
                  <div className="grid grid-cols-2 gap-2 mt-2 justify-center">
                    {letters.slice(24, 26).map((letter) => (
                      <Button
                        key={letter.letter}
                        variant={selectedArLetter === letter.letter ? "default" : "outline"}
                        size="sm"
                        onClick={() => setSelectedArLetter(letter.letter)}
                        className="w-10 h-10"
                      >
                        {letter.letter}
                      </Button>
                    ))}
                  </div>
                </div>
              </CardContent>
            </Card>

            {selectedArLetter && (
              <Card className="bg-gradient-to-br from-purple-100 to-blue-100">
                <CardContent className="p-6">
                  <div className="text-center">
                    <div className="bg-white rounded-lg p-6 mb-4 shadow-lg">
                      <div className="text-6xl mb-4">{letters.find((l) => l.letter === selectedArLetter)?.gesture}</div>
                      <div className="w-20 h-20 bg-yellow-400 rounded-lg flex items-center justify-center mx-auto mb-4">
                        <div className="text-2xl">👨</div>
                      </div>
                      <h3 className="text-2xl font-bold text-gray-800 mb-2">Huruf {selectedArLetter}</h3>
                      <p className="text-gray-600 mb-4">
                        {letters.find((l) => l.letter === selectedArLetter)?.description}
                      </p>
                    </div>

                    <div className="flex justify-center space-x-4">
                      <Button variant="outline" size="sm">
                        <RotateCcw className="h-4 w-4 mr-2" />
                        Ulangi
                      </Button>
                      <Button variant="outline" size="sm">
                        <Volume2 className="h-4 w-4 mr-2" />
                        Suara
                      </Button>
                      <Button variant="outline" size="sm">
                        <Play className="h-4 w-4 mr-2" />
                        Animasi
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            )}

            <div className="flex justify-center mt-6">
              <Button variant="outline" onClick={() => setArMode("idle")}>
                <ArrowLeft className="h-4 w-4 mr-2" />
                Kembali ke Menu AR
              </Button>
            </div>
          </>
        )}
      </div>
    </div>
  )

  const QuizScreen = () => {
    const startQuiz = (mode: QuizMode) => {
      setQuizMode(mode)
      setCurrentQuizQuestion(0)
      setQuizScore(0)
      setQuizAnswers([])
      setShowQuizResult(false)
    }

    const answerQuestion = (answerIndex: number) => {
      const isCorrect = answerIndex === quizQuestions[currentQuizQuestion].correct
      const newAnswers = [...quizAnswers, isCorrect]
      setQuizAnswers(newAnswers)

      if (isCorrect) {
        setQuizScore(quizScore + 1)
      }

      if (currentQuizQuestion < quizQuestions.length - 1) {
        setCurrentQuizQuestion(currentQuizQuestion + 1)
      } else {
        setShowQuizResult(true)
        const finalScore = quizScore + (isCorrect ? 1 : 0)
        const newProgress = {
          ...userProgress,
          totalPoints: userProgress.totalPoints + finalScore * 5,
          quizScores: { ...userProgress.quizScores, [quizMode]: finalScore },
        }
        setUserProgress(newProgress)

        if (finalScore === quizQuestions.length) {
          const newAchievements = [...newProgress.achievements]
          if (!newAchievements.includes("quiz_champion")) {
            newAchievements.push("quiz_champion")
            setUserProgress({ ...newProgress, achievements: newAchievements })
          }
        }
      }
    }

    if (showQuizResult) {
      const finalScore = quizScore
      const percentage = (finalScore / quizQuestions.length) * 100

      return (
        <div className="min-h-screen bg-gradient-to-br from-green-200 to-green-300 p-6">
          <div className="max-w-md mx-auto">
            <Card className="bg-white">
              <CardContent className="p-8 text-center">
                <div className="text-6xl mb-4">{percentage >= 80 ? "🏆" : percentage >= 60 ? "🎉" : "💪"}</div>
                <h2 className="text-2xl font-bold mb-4">Hasil Kuis</h2>
                <div className="text-4xl font-bold text-green-600 mb-2">
                  {finalScore}/{quizQuestions.length}
                </div>
                <p className="text-gray-600 mb-6">
                  {percentage >= 80 ? "Luar biasa!" : percentage >= 60 ? "Bagus sekali!" : "Terus berlatih!"}
                </p>

                <div className="space-y-2 mb-6">
                  {quizAnswers.map((correct, index) => (
                    <div key={index} className="flex items-center justify-between p-2 bg-gray-50 rounded">
                      <span>Soal {index + 1}</span>
                      {correct ? (
                        <CheckCircle className="h-5 w-5 text-green-600" />
                      ) : (
                        <XCircle className="h-5 w-5 text-red-600" />
                      )}
                    </div>
                  ))}
                </div>

                <div className="flex justify-center space-x-4">
                  <Button onClick={() => startQuiz(quizMode)} className="bg-green-500 hover:bg-green-600">
                    <RefreshCw className="h-4 w-4 mr-2" />
                    Ulangi Kuis
                  </Button>
                  <Button variant="outline" onClick={() => setCurrentScreen("quiz")}>
                    <ArrowLeft className="h-4 w-4 mr-2" />
                    Menu Kuis
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      )
    }

    if (currentQuizQuestion < quizQuestions.length && quizMode) {
      const question = quizQuestions[currentQuizQuestion]

      return (
        <div className="min-h-screen bg-gradient-to-br from-green-200 to-green-300 p-6">
          <div className="max-w-md mx-auto">
            <div className="flex items-center justify-between mb-6">
              <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("quiz")}>
                <ArrowLeft className="h-6 w-6" />
              </Button>
              <h1 className="text-xl font-bold text-gray-800">Kuis Isyarat</h1>
              <div className="text-sm text-gray-600">
                {currentQuizQuestion + 1}/{quizQuestions.length}
              </div>
            </div>

            <Card className="bg-white mb-6">
              <CardContent className="p-6">
                <div className="text-center mb-6">
                  <div className="flex justify-between items-center mb-4">
                    <Badge variant="outline">Soal {currentQuizQuestion + 1}</Badge>
                    <div className="text-sm text-gray-600">Skor: {quizScore}</div>
                  </div>
                  <Progress value={(currentQuizQuestion / quizQuestions.length) * 100} className="mb-4" />
                </div>

                <div className="text-center mb-6">
                  {question.type === "sign-to-letter" && question.gesture && (
                    <div className="text-6xl mb-4">{question.gesture}</div>
                  )}
                  <h3 className="text-lg font-semibold mb-4">{question.question}</h3>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  {question.options.map((option, index) => (
                    <Button
                      key={index}
                      variant="outline"
                      className="h-16 text-lg font-semibold hover:bg-green-100 bg-transparent"
                      onClick={() => answerQuestion(index)}
                    >
                      {question.type === "letter-to-sign" &&
                      typeof option === "string" &&
                      option.length === 1 &&
                      option.match(/[👆✋👌☝️✊🤙✌️👇🤞👈]/u) ? (
                        <span className="text-3xl">{option}</span>
                      ) : (
                        option
                      )}
                    </Button>
                  ))}
                </div>
              </CardContent>
            </Card>

            <div className="text-center text-gray-600">
              <p className="text-sm">Pilih jawaban yang benar</p>
            </div>
          </div>
        </div>
      )
    }

    return (
      <div className="min-h-screen bg-gradient-to-br from-green-200 to-green-300 p-6">
        <div className="max-w-md mx-auto">
          <div className="flex items-center justify-between mb-6">
            <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("home")}>
              <ArrowLeft className="h-6 w-6" />
            </Button>
            <h1 className="text-2xl font-bold text-gray-800">Kuis Isyarat</h1>
            <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("home")}>
              <Home className="h-6 w-6" />
            </Button>
          </div>

          <Card className="bg-white mb-6">
            <CardContent className="p-6 text-center">
              <Trophy className="h-16 w-16 mx-auto mb-4 text-yellow-500" />
              <h2 className="text-xl font-semibold mb-2">Uji Kemampuan Anda!</h2>
              <p className="text-gray-600 mb-4">Pilih mode kuis untuk menguji pemahaman bahasa isyarat Anda</p>
            </CardContent>
          </Card>

          <div className="space-y-4">
            <Card
              className="cursor-pointer hover:shadow-lg transition-all transform hover:scale-105"
              onClick={() => startQuiz("letter-to-sign")}
            >
              <CardContent className="p-6">
                <div className="flex items-center space-x-4">
                  <div className="text-3xl">📝</div>
                  <div className="flex-1">
                    <h3 className="font-semibold text-gray-800">Huruf ke Isyarat</h3>
                    <p className="text-sm text-gray-600">Pilih isyarat yang benar untuk huruf yang diberikan</p>
                  </div>
                  <ArrowRight className="h-5 w-5 text-gray-400" />
                </div>
              </CardContent>
            </Card>

            <Card
              className="cursor-pointer hover:shadow-lg transition-all transform hover:scale-105"
              onClick={() => startQuiz("sign-to-letter")}
            >
              <CardContent className="p-6">
                <div className="flex items-center space-x-4">
                  <div className="text-3xl">🤟</div>
                  <div className="flex-1">
                    <h3 className="font-semibold text-gray-800">Isyarat ke Huruf</h3>
                    <p className="text-sm text-gray-600">Tebak huruf dari isyarat yang ditampilkan</p>
                  </div>
                  <ArrowRight className="h-5 w-5 text-gray-400" />
                </div>
              </CardContent>
            </Card>

            <Card
              className="cursor-pointer hover:shadow-lg transition-all transform hover:scale-105"
              onClick={() => startQuiz("mixed")}
            >
              <CardContent className="p-6">
                <div className="flex items-center space-x-4">
                  <div className="text-3xl">🎯</div>
                  <div className="flex-1">
                    <h3 className="font-semibold text-gray-800">Mode Campuran</h3>
                    <p className="text-sm text-gray-600">Kombinasi semua jenis pertanyaan</p>
                  </div>
                  <ArrowRight className="h-5 w-5 text-gray-400" />
                </div>
              </CardContent>
            </Card>
          </div>

          {Object.keys(userProgress.quizScores).length > 0 && (
            <Card className="mt-6 bg-blue-50">
              <CardContent className="p-6">
                <h3 className="font-semibold text-blue-800 mb-3">📊 Riwayat Skor:</h3>
                <div className="space-y-2">
                  {Object.entries(userProgress.quizScores).map(([mode, score]) => (
                    <div key={mode} className="flex justify-between items-center">
                      <span className="text-sm text-blue-700 capitalize">{mode.replace("-", " ke ")}</span>
                      <Badge variant="outline">{score}/5</Badge>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    )
  }

  const ProfileScreen = () => (
    <div className="min-h-screen bg-gradient-to-br from-indigo-200 to-indigo-300 p-6">
      <div className="max-w-md mx-auto">
        <div className="flex items-center justify-between mb-6">
          <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("home")}>
            <ArrowLeft className="h-6 w-6" />
          </Button>
          <h1 className="text-2xl font-bold text-gray-800">Profil Pengguna</h1>
          <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("achievements")}>
            <Trophy className="h-6 w-6" />
          </Button>
        </div>

        <Card className="bg-white mb-6">
          <CardContent className="p-6 text-center">
            <div className="w-20 h-20 bg-indigo-500 rounded-full flex items-center justify-center mx-auto mb-4">
              <Users className="h-10 w-10 text-white" />
            </div>
            <h2 className="text-xl font-semibold mb-2">Pelajar SIBI</h2>
            <p className="text-gray-600 mb-4">Bergabung sejak hari ini</p>
            <div className="flex justify-center space-x-6">
              <div className="text-center">
                <div className="text-2xl font-bold text-indigo-600">{userProgress.totalPoints}</div>
                <div className="text-xs text-gray-600">Total Poin</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold text-green-600">{userProgress.lettersLearned.length}</div>
                <div className="text-xs text-gray-600">Huruf Dipelajari</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold text-yellow-600">{userProgress.achievements.length}</div>
                <div className="text-xs text-gray-600">Pencapaian</div>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="bg-white mb-6">
          <CardHeader>
            <CardTitle className="text-lg">Progress Pembelajaran</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div>
                <div className="flex justify-between mb-2">
                  <span className="text-sm font-medium">Huruf A-Z</span>
                  <span className="text-sm text-gray-600">{userProgress.lettersLearned.length}/26</span>
                </div>
                <Progress value={(userProgress.lettersLearned.length / 26) * 100} />
              </div>

              <div>
                <div className="flex justify-between mb-2">
                  <span className="text-sm font-medium">Kuis Diselesaikan</span>
                  <span className="text-sm text-gray-600">{Object.keys(userProgress.quizScores).length}/3</span>
                </div>
                <Progress value={(Object.keys(userProgress.quizScores).length / 3) * 100} />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="bg-white">
          <CardHeader>
            <CardTitle className="text-lg">Statistik</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 gap-4">
              <div className="text-center p-4 bg-blue-50 rounded-lg">
                <div className="text-lg font-bold text-blue-600">{userProgress.currentStreak}</div>
                <div className="text-xs text-blue-800">Streak Saat Ini</div>
              </div>
              <div className="text-center p-4 bg-green-50 rounded-lg">
                <div className="text-lg font-bold text-green-600">{userProgress.bestStreak}</div>
                <div className="text-xs text-green-800">Streak Terbaik</div>
              </div>
              <div className="text-center p-4 bg-yellow-50 rounded-lg">
                <div className="text-lg font-bold text-yellow-600">
                  {Object.values(userProgress.quizScores).reduce((a, b) => a + b, 0)}
                </div>
                <div className="text-xs text-yellow-800">Total Skor Kuis</div>
              </div>
              <div className="text-center p-4 bg-purple-50 rounded-lg">
                <div className="text-lg font-bold text-purple-600">
                  {Math.round((userProgress.lettersLearned.length / 26) * 100)}%
                </div>
                <div className="text-xs text-purple-800">Kemajuan</div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )

  const AchievementsScreen = () => (
    <div className="min-h-screen bg-gradient-to-br from-yellow-200 to-orange-300 p-6">
      <div className="max-w-md mx-auto">
        <div className="flex items-center justify-between mb-6">
          <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("profile")}>
            <ArrowLeft className="h-6 w-6" />
          </Button>
          <h1 className="text-2xl font-bold text-gray-800">Pencapaian</h1>
          <div className="w-10"></div>
        </div>

        <Card className="bg-white mb-6">
          <CardContent className="p-6 text-center">
            <Trophy className="h-16 w-16 mx-auto mb-4 text-yellow-500" />
            <h2 className="text-xl font-semibold mb-2">Koleksi Pencapaian</h2>
            <p className="text-gray-600">
              {userProgress.achievements.length} dari {achievements.length} pencapaian terbuka
            </p>
            <Progress value={(userProgress.achievements.length / achievements.length) * 100} className="mt-4" />
          </CardContent>
        </Card>

        <div className="space-y-4">
          {achievements.map((achievement) => {
            const isUnlocked = userProgress.achievements.includes(achievement.id)
            return (
              <Card
                key={achievement.id}
                className={`${isUnlocked ? "bg-yellow-50 border-yellow-200" : "bg-gray-50 border-gray-200"}`}
              >
                <CardContent className="p-6">
                  <div className="flex items-center space-x-4">
                    <div className={`text-3xl ${isUnlocked ? "" : "grayscale opacity-50"}`}>{achievement.icon}</div>
                    <div className="flex-1">
                      <h3 className={`font-semibold ${isUnlocked ? "text-yellow-800" : "text-gray-500"}`}>
                        {achievement.name}
                      </h3>
                      <p className={`text-sm ${isUnlocked ? "text-yellow-600" : "text-gray-400"}`}>
                        {achievement.description}
                      </p>
                    </div>
                    {isUnlocked && <CheckCircle className="h-6 w-6 text-yellow-600" />}
                  </div>
                </CardContent>
              </Card>
            )
          })}
        </div>
      </div>
    </div>
  )

  const AboutScreen = () => (
    <div className="min-h-screen bg-gradient-to-br from-purple-200 to-purple-300 p-6">
      <div className="max-w-md mx-auto">
        <div className="flex items-center justify-between mb-6">
          <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("home")}>
            <ArrowLeft className="h-6 w-6" />
          </Button>
          <h1 className="text-2xl font-bold text-gray-800">Tentang Aplikasi</h1>
          <div className="w-10"></div>
        </div>

        <Card className="bg-white mb-6">
          <CardContent className="p-6">
            <div className="text-center mb-6">
              <div className="text-6xl mb-4">🤟</div>
              <h2 className="text-2xl font-bold text-gray-800 mb-2">Isyarat Pintar</h2>
              <p className="text-gray-600">Versi 1.0.0</p>
              <Badge className="mt-2">SIBI Certified</Badge>
            </div>

            <div className="space-y-6">
              <div>
                <h3 className="font-semibold text-gray-800 mb-3 flex items-center">
                  <BookOpen className="h-5 w-5 mr-2" />
                  Tentang SIBI
                </h3>
                <p className="text-sm text-gray-600 leading-relaxed">
                  Sistem Isyarat Bahasa Indonesia (SIBI) adalah sistem komunikasi visual yang digunakan oleh komunitas
                  tunarungu di Indonesia. SIBI dikembangkan untuk memfasilitasi komunikasi dan pendidikan bagi
                  penyandang tunarungu dengan menggunakan gerakan tangan, ekspresi wajah, dan bahasa tubuh.
                </p>
              </div>

              <div>
                <h3 className="font-semibold text-gray-800 mb-3 flex items-center">
                  <Star className="h-5 w-5 mr-2" />
                  Fitur Aplikasi
                </h3>
                <ul className="text-sm text-gray-600 space-y-2">
                  <li className="flex items-start">
                    <CheckCircle className="h-4 w-4 mr-2 mt-0.5 text-green-600 flex-shrink-0" />
                    Pembelajaran huruf A-Z dengan isyarat SIBI standar
                  </li>
                  <li className="flex items-start">
                    <CheckCircle className="h-4 w-4 mr-2 mt-0.5 text-green-600 flex-shrink-0" />
                    Mode AR untuk visualisasi 3D dan pembelajaran interaktif
                  </li>
                  <li className="flex items-start">
                    <CheckCircle className="h-4 w-4 mr-2 mt-0.5 text-green-600 flex-shrink-0" />
                    Sistem kuis dengan berbagai mode untuk menguji kemampuan
                  </li>
                  <li className="flex items-start">
                    <CheckCircle className="h-4 w-4 mr-2 mt-0.5 text-green-600 flex-shrink-0" />
                    Audio pronunciation untuk setiap huruf
                  </li>
                  <li className="flex items-start">
                    <CheckCircle className="h-4 w-4 mr-2 mt-0.5 text-green-600 flex-shrink-0" />
                    Sistem pencapaian dan progress tracking
                  </li>
                  <li className="flex items-start">
                    <CheckCircle className="h-4 w-4 mr-2 mt-0.5 text-green-600 flex-shrink-0" />
                    Tips dan panduan untuk setiap gerakan isyarat
                  </li>
                </ul>
              </div>

              <div>
                <h3 className="font-semibold text-gray-800 mb-3 flex items-center">
                  <Heart className="h-5 w-5 mr-2" />
                  Tujuan Aplikasi
                </h3>
                <p className="text-sm text-gray-600 leading-relaxed">
                  Aplikasi ini dikembangkan dengan tujuan membantu pembelajaran bahasa isyarat bagi komunitas tunarungu
                  Indonesia, serta meningkatkan kesadaran masyarakat umum tentang pentingnya komunikasi inklusif. Dengan
                  teknologi AR dan metode pembelajaran interaktif, kami berharap dapat membuat pembelajaran bahasa
                  isyarat menjadi lebih mudah, menyenangkan, dan efektif.
                </p>
              </div>

              <div>
                <h3 className="font-semibold text-gray-800 mb-3 flex items-center">
                  <Users className="h-5 w-5 mr-2" />
                  Pengembang
                </h3>
                <p className="text-sm text-gray-600 leading-relaxed">
                  Dikembangkan dengan dedikasi untuk mendukung inklusi dan aksesibilitas dalam pendidikan. Aplikasi ini
                  merupakan hasil kolaborasi dengan komunitas tunarungu dan ahli bahasa isyarat untuk memastikan akurasi
                  dan kualitas pembelajaran.
                </p>
              </div>

              <div className="border-t pt-4">
                <div className="text-center">
                  <p className="text-xs text-gray-500 mb-2">© 2024 Isyarat Pintar. Semua hak dilindungi.</p>
                  <div className="flex justify-center space-x-4 text-xs text-gray-500">
                    <span>Kebijakan Privasi</span>
                    <span>•</span>
                    <span>Syarat Penggunaan</span>
                    <span>•</span>
                    <span>Bantuan</span>
                  </div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )

  const screens = {
    splash: <SplashScreen />,
    home: <HomeScreen />,
    learn: <LearnScreen />,
    ar: <ARScreen />,
    quiz: <QuizScreen />,
    about: <AboutScreen />,
    profile: <ProfileScreen />,
    achievements: <AchievementsScreen />,
  }

  return <div className="max-w-md mx-auto bg-white min-h-screen">{screens[currentScreen]}</div>
}
