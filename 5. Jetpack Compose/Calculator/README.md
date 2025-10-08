# Calculator Android App

A modern calculator app built with Jetpack Compose featuring a clean, intuitive interface and real-time calculations.

## Features

### UI Components

- **Real-time Display**: Shows both the equation being typed and the calculated result
- **4x5 Button Grid**: Organized layout with numbers, operators, and functions
- **Modern Design**: Rounded buttons with elevation effects similar to FloatingActionButton
- **Color-coded Buttons**:
  - 🔴 Red: Clear functions ('C', 'AC')
  - 🔵 Gray: Parentheses ('(', ')')
  - 🟠 Orange: Operators (+, -, ×, ÷, =)
  - 🟢 Teal: Numbers and decimal point

### Functionality

- **Basic Operations**: Addition, subtraction, multiplication, division
- **Parentheses Support**: For complex mathematical expressions
- **Decimal Numbers**: Full decimal point support
- **Clear Functions**:
  - 'AC' (All Clear): Clears entire equation and result
  - 'C' (Clear): Removes last entered character
- **Real-time Calculation**: Results update automatically as you type
- **Smart Result Display**: Removes trailing '.0' for whole numbers

### Technical Implementation

- **Architecture**: MVVM pattern with ViewModel
- **State Management**: LiveData for UI state observation
- **Expression Evaluation**: Mozilla Rhino JavaScript engine for safe mathematical expression evaluation
- **UI Framework**: 100% Jetpack Compose
- **Theme**: Material 3 Design with custom dark theme

## Button Layout

```
AC  C   (   )   ÷
7   8   9   ×
4   5   6   -
1   2   3   +
0   .       =
```

## Usage

1. Tap number buttons to enter numbers
2. Tap operator buttons (+, -, ×, ÷) for mathematical operations
3. Use parentheses for complex expressions
4. The result displays in real-time as you type
5. Tap '=' to move the result to the equation field
6. Use 'C' to delete the last character or 'AC' to clear everything

## Dependencies

- **Jetpack Compose**: Modern UI toolkit
- **ViewModel**: For state management and business logic
- **LiveData**: For reactive UI updates
- **Mozilla Rhino**: Safe JavaScript engine for expression evaluation

## Project Structure

```
com.komputerkit.calculator/
├── MainActivity.kt              # Main activity and app entry point
├── CalculatorViewModel.kt       # Business logic and state management
├── CalculatorScreen.kt         # Main calculator UI composable
├── CalculatorButton.kt         # Reusable button component and styling
└── ui/theme/                   # Theme and styling configuration
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```
