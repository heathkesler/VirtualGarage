export const vehicles = [
  {
    id: 1,
    name: "2024 BMW M3 Competition",
    type: "Sport Sedan",
    year: 2024,
    image: "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800&h=600&fit=crop&crop=center",
    mileage: "1,250 miles",
    value: "$89,500",
    status: "Excellent",
    tags: ["Performance", "Daily Driver"]
  },
  {
    id: 2,
    name: "1969 Ford Mustang Boss 429",
    type: "Classic Muscle",
    year: 1969,
    image: "https://images.unsplash.com/photo-1494905998402-395d579af36f?w=800&h=600&fit=crop&crop=center",
    mileage: "45,000 miles",
    value: "$165,000",
    status: "Pristine",
    tags: ["Classic", "Collector"]
  },
  {
    id: 3,
    name: "2023 Porsche 911 GT3",
    type: "Sports Car",
    year: 2023,
    image: "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=600&fit=crop&crop=center",
    mileage: "3,200 miles",
    value: "$195,000",
    status: "Excellent",
    tags: ["Track", "Performance"]
  },
  {
    id: 4,
    name: "2024 Tesla Model S Plaid",
    type: "Electric Sedan",
    year: 2024,
    image: "https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=800&h=600&fit=crop&crop=center",
    mileage: "8,500 miles",
    value: "$105,000",
    status: "Like New",
    tags: ["Electric", "Tech"]
  },
  {
    id: 5,
    name: "2022 McLaren 720S",
    type: "Supercar",
    year: 2022,
    image: "https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=800&h=600&fit=crop&crop=center",
    mileage: "2,100 miles",
    value: "$285,000",
    status: "Pristine",
    tags: ["Exotic", "Performance"]
  },
  {
    id: 6,
    name: "1965 Shelby Cobra 427",
    type: "Classic Roadster",
    year: 1965,
    image: "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800&h=600&fit=crop&crop=center",
    mileage: "12,000 miles",
    value: "$750,000",
    status: "Concours",
    tags: ["Classic", "Rare", "Investment"]
  }
];

export const garageStats = {
  totalVehicles: vehicles.length,
  totalValue: vehicles.reduce((sum, vehicle) => {
    const value = parseInt(vehicle.value.replace(/[$,]/g, ''));
    return sum + value;
  }, 0),
  avgValue: function() {
    return Math.round(this.totalValue / this.totalVehicles);
  }
};